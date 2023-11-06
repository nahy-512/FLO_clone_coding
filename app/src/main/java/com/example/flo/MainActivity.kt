package com.example.flo

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), AlbumClickListener {

    companion object { const val STRING_INTENT_KEY = "song_key" }

    lateinit var binding: ActivityMainBinding

    private var timer: Timer? = null
    private var mediaPlayer: MediaPlayer? = null

    private var songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase

    var nowPos = 1

    private val getResultText = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val returnSong = result.data?.getStringExtra(STRING_INTENT_KEY)
            // 받아온 앨범 제목으로 토스트 메시지 띄우기
            Toast.makeText(this, returnSong, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FLO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // songDB 초기화 -> songDB의 인스턴스를 받아줌
        songDB = SongDatabase.getInstance(this)!!

        inputDummySongs()
        initBottomNavigation()
    }

    override fun onStart() {
        super.onStart()

        initSong()
        initClickListener()
    }

    override fun onPause() {
        super.onPause()

        /* 음악 재생 중지 및 스레드 종료 */
        mediaPlayer?.pause()
        timer?.isPlaying = false
        timer?.interrupt()
        Thread.sleep(50)
        // 곡 재생 정보 업데이트
        updatePlayingData()
    }

    override fun onDestroy() {
        super.onDestroy()

        /* 스레드 종료 및 리소스 해제 */
        initializeMusic(false)
        // 노래 재생 정지
        songDB.songDao().update(songs[nowPos].copy(isPlaying = false))
    }

    private fun updatePlayingData() {
        //TODO: 노래 재생 시간 DB에 업데이트
        val second = ((binding.mainMiniplayerProgressSb.progress * songs[nowPos].playTime)/100)/1000 // 재생 시간을 초 단위로 변환
//        Log.d("onPause()", "현재 재생 시간: ${song.second}")

        // 재생 시간 업데이트
        songDB.songDao().updatePlayingStateById(second, songs[nowPos].isPlaying, songs[nowPos].id)
    }

    private fun initSong() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 1)

        // 현재 곡 위치 계산하기
        nowPos = getPlayerSongPosition(songId)

        // 미니플레이어 및 미디어플레이어 설정
        setPlayer(songs[nowPos])
    }

    private fun getPlayerSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 1
    }

    private fun moveSong(direct: Int) {
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        // 음악 초기화
        initializeMusic(true)
    }

    private fun initializeMusic(isPlaying: Boolean) {
        // 리소스 해제
        timer?.interrupt()
        mediaPlayer?.release() // 미디어플레이어가 가지고 있던 리소스 해제
        mediaPlayer = null // 미디어 플레이어 해제
        // 재생 시간 초기화
        songs[nowPos].second  = 0
        songs[nowPos].isPlaying = isPlaying
        songDB.songDao().update(songs[nowPos].copy(isPlaying = isPlaying, second =  songs[nowPos].second))
        runOnUiThread {
            setPlayerStatus(isPlaying)
            binding.mainMiniplayerProgressSb.progress = songs[nowPos].second
            // 새로운 노래 재시작 준비
            setPlayer(songs[nowPos])
        }
        // 스레드 종료
        Thread.sleep(50)
    }

    private fun setPlayer(song: Song) {
        // 미니플레이어 뷰 랜더링
        setMiniPlayer(song)

        // 음악 파일을 찾아서 넣어줌
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        // 재생 버튼 및 음악 재생 상태 지정
        setPlayerStatus(song.isPlaying)

        // 이전 재생 시간 반영
        mediaPlayer?.seekTo(song.second * 1000)
        // 음악 종료 시간 설정
        song.playTime = mediaPlayer?.duration!! / 1000

        // DB 저장
        songDB.songDao().update(song.copy(playTime = song.playTime))

        // 타이머 시작
        startTimer(song.playTime)
    }

    private fun startTimer(playTime: Int) {
        timer = Timer(playTime, songs[nowPos].isPlaying)
        timer?.start()
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
        timer?.isPlaying = isPlaying

        if (isPlaying) { // 재생 상태
            binding.mainMiniplayerPlayBtn.visibility = View.GONE
            binding.mainMiniplayerPauseBtn.visibility = View.VISIBLE
            // 음악 재생
            mediaPlayer?.start()
        } else { // 정지 상태
            binding.mainMiniplayerPlayBtn.visibility = View.VISIBLE
            binding.mainMiniplayerPauseBtn.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) { // 음악 재생 중일 떄
                // 음악 중지
                mediaPlayer?.pause()
            }
        }
    }

    private fun setMiniPlayer(song: Song) {
        with(binding) {
            mainMiniplayerTitleTv.text = song.title
            mainMiniplayerSingerTv.text = song.singer
            mainMiniplayerProgressSb.progress= (song.second * 100000) / song.playTime
        }
    }

    private fun initClickListener() {
//        val song = Song(binding.mainMiniplayerTitleTv.text.toString(), binding.mainMiniplayerSingerTv.text.toString(), 0, 60, false, "music_lilac")
        Log.d("Song", songs[nowPos].title + songs[nowPos].singer)

        binding.mainPlayerCl.setOnClickListener {
            // songId로 데이터를 넘겨줌
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id).apply()

            val intent = Intent(this, SongActivity::class.java)
            // SongActivity에서 돌아올 때 받은 앨범 제목
            getResultText.launch(intent)
        }

        /* 재생 버튼 */
        binding.mainMiniplayerPlayBtn.setOnClickListener {
            setPlayerStatus(true)
        }

        /* 정지 버튼 */
        binding.mainMiniplayerPauseBtn.setOnClickListener {
            setPlayerStatus(false)
        }

        /* 이전 재생 버튼 */
        binding.mainMiniplayerPrevBtn.setOnClickListener {
            moveSong(-1)
        }

        /* 다음 재생 버튼 */
        binding.mainMiniplayerNextBtn.setOnClickListener {
            moveSong(+1)
        }
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onAlbumReceived(data: Album) {
        Log.e("MainActivity", "albumData: ${data}")
        // HomeFragment에서 클릭한 오늘 발매 음악의 첫 번째 수록곡 정보를 받아옴
        val songData = data.song?.get(0)

        // 미니플레이어 뷰 업데이트
        if (songData?.title != null) {
            binding.mainMiniplayerTitleTv.text = songData.title
            binding.mainMiniplayerSingerTv.text = songData.singer
            // song 데이터 업데이트
            //TODO: 아이디로 받아올 것. 첫 번쨰 수록곡으로 받아오므로 여기선 항상 nowPos 1부터 시작
            songs[nowPos] = songData
        } else { // 수록곡 정보가 없으면 앨범 제목과 가수를 넣어줌
            binding.mainMiniplayerTitleTv.text = data.title
            binding.mainMiniplayerSingerTv.text = data.singer
            // song 제목, 가수, 이미지 업데이트
            songs[nowPos].title = data.title.toString()
            songs[nowPos].singer = data.singer.toString()
            songs[nowPos].coverImg = data.coverImg
        }
    }

    private fun inputDummySongs() {
        songs = songDB.songDao().getSongs() as ArrayList<Song>

        // 저장된 songs 데이터가 있다면 바로 리턴
        if (songs.isNotEmpty()) return

        // songs가 비어있다면 더미데이터를 넣어줌
        songDB.songDao().insert(
            Song("LILAC", "아이유 (IU)", R.drawable.img_album_exp2,0, 180, false, "music_lilac", false)
        )
        songDB.songDao().insert(
            Song("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3,0, 180, false, "music_next", false)
        )
        songDB.songDao().insert(
            Song("달", "악뮤 (AKMU)", R.drawable.img_album_exp7,0, 180, false, "music_moon", false)
        )
        songDB.songDao().insert(
            Song("Blueming", "아이유 (IU)", R.drawable.img_album_exp10,0, 180, false, "music_blueming", false)
        )
        songDB.songDao().insert(
            Song("flu", "아이유 (IU)", R.drawable.img_album_exp2,0, 180, false, "music_flu", false)
        )
        songDB.songDao().insert(
            Song("작은 것들을 위한 시", "방탄소년단 (BTS)", R.drawable.img_album_exp4,0, 180, false, "music_butter")
        )
        songDB.songDao().insert(
            Song("Island", "위너 (WINNER)", R.drawable.img_album_exp9,0, 180, false, "music_island")
        )
        songDB.songDao().insert(
            Song("TOMBOY", "(여자)아이들", R.drawable.img_album_exp8,0, 180, false, "music_tomboy")
        )
        songDB.songDao().insert(
            Song("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp,0, 180, false, "music_butter")
        )
        songDB.songDao().insert(
            Song("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6,0, 180, false, "music_lilac")
        )
        songDB.songDao().insert(
            Song("뿜뿜", "모모랜드 (MOMOLANDS)", R.drawable.img_album_exp5,0, 180, false, "music_bboom")
        )

        // 데이터가 잘 들어왔는지 확인
//        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", songs.toString())
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true): Thread() {

        private var second: Int = songs[nowPos].second // 이전 시작 시간부터 이어서 설정
        private var mills: Float = second.toFloat() * 1000

        override fun run() {
            super.run()
            try {
                // 타이머는 계속 진행되어야 함
                while (true) {
                    if (second >= playTime) { // 노래 재생 시간이 다 끝나면 종료
//                        when (repeatState) {
//                            1 -> initializeMusic(true) // 한곡 재생 상태에서는 바로 초기화해서 다시 재생
//                            else -> initializeMusic(false) // 그 이외의 경우에는 음악 재생 중지
//                        }
                        break
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        // 시간이 증가했으므로 seekbar에도 적용
                        runOnUiThread {
                            binding.mainMiniplayerProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        // 진행 시간 증가
                        if (mills % 1000 == 0f) {
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}