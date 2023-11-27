package com.example.flo.ui.main

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flo.ui.main.home.AlbumClickListener
import com.example.flo.ui.main.home.HomeFragment
import com.example.flo.ui.main.locker.LockerFragment
import com.example.flo.ui.main.look.LookFragment
import com.example.flo.R
import com.example.flo.ui.main.search.SearchFragment
import com.example.flo.ui.song.SongActivity
import com.example.flo.data.local.SongDatabase
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AlbumClickListener {

    companion object { const val STRING_INTENT_KEY = "song_key" }

    lateinit var binding: ActivityMainBinding

    private var timer: Timer? = null
    private var mediaPlayer: MediaPlayer? = null

    private var songs = arrayListOf<Song>()
    private var albums = arrayListOf<Album>()
    lateinit var songDB: SongDatabase

    private var albumIdx: Int = 1
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

        inputDummyAlbums()
        inputDummySongs()
        initBottomNavigation()

        // jwt 확인
        Log.d("MAIN/JWT_TO_SERVICE", getJwt().toString())
    }

    override fun onStart() {
        super.onStart()

        initSong()
        initClickListener()
    }

    override fun onResume() {
        super.onResume()

        albumIdx = getAlbumIdx()
    }

    override fun onPause() {
        super.onPause()

        /* 음악 재생 중지 및 스레드 종료 */
        mediaPlayer?.pause()
        timer?.isPlaying = false
        timer?.interrupt()
        Thread.sleep(50)
        // 곡 재생 정보 업데이트
        savedId()
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
        // 저장된 songId를 가져옴
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 1)
        Log.d("MainActivity", "저장된 songIdx: $songId")

        // 저장된 albumId를 다시 가져옴
        albumIdx = getAlbumIdx()
        songs = songDB.songDao().getSongsInAlbum(albumIdx) as ArrayList<Song>

        // 현재 곡 위치 계산하기
        nowPos = getPlayerSongPosition(songId)
        songs[nowPos] = songDB.songDao().getSong(songId)
        Log.d("MainActivity", "songs[nowPos] = ${songs[nowPos]}")

        // 미니플레이어 및 미디어플레이어 설정
        setPlayer(songs[nowPos])

        // 흐르는 텍스트 처리
        binding.mainMiniplayerTitleTv.isSelected = true
    }

    private fun getPlayerSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 1
    }

    private fun getPlayerAlbumPosition(albumId: Int): Int {
        for (i in albums.indices) {
            if (albums[i].id == albumId) {
                return i
            }
        }
        return 1
    }

    private fun moveSong(direct: Int) {
        if (nowPos + direct < 0) {
            // 이전 앨범으로 이동
            Toast.makeText(this, "이전 앨범", Toast.LENGTH_SHORT).show()
            moveAlbum(-1)
            return
        }
        if (nowPos + direct >= songs.size) {
            // 다음 앨범으로 이동
            Toast.makeText(this, "다음 앨범", Toast.LENGTH_SHORT).show()
            moveAlbum(+1)
            return
        }
        nowPos += direct

        // 음악 초기화
        initializeMusic(true)
    }

    private fun moveAlbum(direct: Int) { // 앨범을 이동하는 경우
        val nowAlbumPos = getPlayerAlbumPosition(albumIdx)
        if (nowAlbumPos + direct < 0) {
            Toast.makeText(this, "first album", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowAlbumPos + direct >= albums.size) {
            Toast.makeText(this, "last album", Toast.LENGTH_SHORT).show()
            return
        }

        // 앨범 아이디 업데이트
        albumIdx += direct
        // 해당 앨범의 수록곡으로 songs 업데이트
        songs = songDB.songDao().getSongsInAlbum(albumIdx) as ArrayList<Song>
        // nowPos 업데이트 (다음 앨범이면 첫 번째 수록곡부터, 이전 앨범이면 마지막 수록곡부터 재생)
        nowPos = if (direct > 0) 0 else songs.size - 1
        // 앨범 이동 후 음악도 초기화
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
        Log.d("MainActivity", "setPlayer - songId: ${song.id}")
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
        Log.d("Song", "${songs[nowPos].title} - ${songs[nowPos].singer}")

        binding.mainPlayerCl.setOnClickListener {
            savedId()
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

    private fun getJwt(): String? {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)

        return spf!!.getString("jwt", "")
    }

    private fun savedId() {
        // spf에 데이터를 저장함
        getSharedPreferences("song", MODE_PRIVATE).edit()
            .putInt("songId", songs[nowPos].id)
            .putInt("albumId", albumIdx)
            .apply()
    }

    private fun getAlbumIdx(): Int {
        // spf에 저장된 albumIdx를 가져옴
        return getSharedPreferences("song", MODE_PRIVATE).getInt("albumId", 1)
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

    override fun onAlbumReceived(albumIdx: Int) {
        Log.e("MainActivity", "albumIdx: $albumIdx")
        this.albumIdx = albumIdx
        // HomeFragment에서 클릭한 오늘 발매 음악 앨범의 수록곡 정보를 DB에서 가져옴
        songs = songDB.songDao().getSongsInAlbum(albumIdx) as ArrayList<Song>
        // 이를 현재 재생 곡 정보에 넣어줌
        nowPos = getPlayerSongPosition(songs[0].id)
        songs[nowPos] = songs[0]
        // id 정보 spf에 저장
        savedId()

        // 미니플레이어 제목 가수 업데이트
        binding.mainMiniplayerTitleTv.text = songs[0].title
        binding.mainMiniplayerSingerTv.text = songs[0].singer

        // 재생하던 음악 초기화 및 다시 재생
        initializeMusic(true)
    }

    private fun inputDummyAlbums() {
        // DB로부터 album 데이터를 모두 조회해옴 (LiveData 초기화)
        albums = songDB.albumDao().getAllAlbums() as ArrayList<Album>

        if (albums.isNotEmpty())
            return
        else {
            // 앨범 데이터가 없을 때의 처리
            Thread{
                // albums가 비어있다면 더미데이터를 넣어줌
                songDB.albumDao().apply {
                    insert(Album("IU 5th Album 'LILAC'", "아이유 (IU)", R.drawable.img_album_exp2))
                    insert(Album("iScreaM Vol. 10 : Next Level Remixes", "에스파 (AESPA)",
                        R.drawable.img_album_exp3
                    ))
                    insert(Album("항해", "악뮤 (AKMU)", R.drawable.img_album_exp7))
                    insert(Album("Love Poem", "아이유 (IU)", R.drawable.img_album_exp10))
                    insert(Album("Map of the Soul", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
                    insert(Album("OUR TWENTY FOR", "위너 (WINNER)", R.drawable.img_album_exp9))
                    insert(Album("I NEVER DIE", "(여자) 아이들", R.drawable.img_album_exp8))
                    insert(Album("Butter (feat. Megan Thee Stallion)", "방탄소년단 (BTS)",
                        R.drawable.img_album_exp
                    ))
                    insert(Album("Great!", "모모랜드 (MOMOLANDS)", R.drawable.img_album_exp5))
                    insert(Album("Weekend", "태연 (TAEYEON)", R.drawable.img_album_exp6))
                }

                // 추가한 데이터를 다시 albums에 넣어줌
                albums = songDB.albumDao().getAllAlbums() as ArrayList<Album>

                // 데이터가 잘 들어왔는지 확인
                val _albums = songDB.albumDao().getAllAlbums()
                Log.d("DB Album data", _albums.toString())
            }.start()
        }
    }

    private fun inputDummySongs() {
        // 앨범 데이터 가져옴
        albumIdx = getAlbumIdx()
        Log.d("MainActivity", "저장된 albumIdx: $albumIdx")
        if (songDB.songDao().getAllSongs().isNotEmpty()) { // 저장된 songs 데이터가 있을 경우
            if (songDB.songDao().getSongsInAlbum(albumIdx).isNotEmpty()) { // 앨범 id로 수록곡 정보 조회해보기
                songs = songDB.songDao().getSongsInAlbum(albumIdx) as ArrayList<Song>
                Log.d("MainActivity", "Songs: $songs")
            } else { // 수록곡 정보가 없다면 전체 곡을 songs에 추가
                songs = songDB.songDao().getAllSongs() as ArrayList<Song>
            }
            return
        }
        else { // songs가 비어있다면 더미데이터를 넣어줌
            Thread {
                songDB.songDao().apply {
                    insert(Song("LILAC", "아이유 (IU)",
                        R.drawable.img_album_exp2,0, 180, false, "music_lilac", false, true, 1))
                    insert(Song("Next Level", "에스파 (AESPA)",
                        R.drawable.img_album_exp3,0, 180, false, "music_next", false, true , 2))
                    insert(Song("달", "악뮤 (AKMU)",
                        R.drawable.img_album_exp7,0, 180, false, "music_moon", false, albumIdx = 3))
                    insert(Song("어떻게 이별까지 사랑하겠어, 널 사랑하는 거지", "악뮤 (AKMU)",
                        R.drawable.img_album_exp7,0, 180, false, "music_moon", false, true, 3))
                    insert(Song("Blueming", "아이유 (IU)",
                        R.drawable.img_album_exp10,0, 180, false, "music_blueming", false, true, 4))
                    insert(Song("unlucky", "아이유 (IU)",
                        R.drawable.img_album_exp10,0, 180, false, "music_blueming", false, false, 4))
                    insert(Song("그 사람", "아이유 (IU)",
                        R.drawable.img_album_exp10,0, 180, false, "music_blueming", false, false, 4))
                    insert(Song("시간의 바깥", "아이유 (IU)",
                        R.drawable.img_album_exp10,0, 180, false, "music_blueming", false, false, 4))
                    insert(Song("자장가", "아이유 (IU)",
                        R.drawable.img_album_exp10,0, 180, false, "music_blueming", false, false, 4))
                    insert(Song("Love poem", "아이유 (IU)",
                        R.drawable.img_album_exp10,0, 180, false, "music_blueming", false, true, 4))
                    insert(Song("Flu", "아이유 (IU)",
                        R.drawable.img_album_exp2,0, 180, false, "music_flu", false, false,1))
                    insert(Song("Coin", "아이유 (IU)",
                        R.drawable.img_album_exp2,0, 180, false, "music_lilac", false, true, 1))
                    insert(Song("봄 안녕 봄", "아이유 (IU)",
                        R.drawable.img_album_exp2,0, 180, false, "music_flu", false, albumIdx = 1))
                    insert(Song("Celebrity", "아이유 (IU)",
                        R.drawable.img_album_exp2,0, 180, false, "music_lilac", false, albumIdx = 1))
                    insert(Song("돌림노래 (Feat. DEAN)", "아이유 (IU)",
                        R.drawable.img_album_exp2,0, 180, false, "music_flu", false, albumIdx = 1))
                    insert(Song("아이와 나의 바다", "아이유 (IU)",
                        R.drawable.img_album_exp2,0, 180, false, "music_lilac", false, albumIdx = 1))
                    insert(Song("어푸 (Au puh)", "아이유 (IU)",
                        R.drawable.img_album_exp2,0, 180, false, "music_flu", false, albumIdx = 1))
                    insert(Song("에필로그", "아이유 (IU)",
                        R.drawable.img_album_exp2,0, 180, false, "music_lilac", false, albumIdx = 1))
                    insert(Song("작은 것들을 위한 시 (Boy With Luv)", "방탄소년단 (BTS)",
                        R.drawable.img_album_exp4,0, 180, false, "music_boywithluv", isTitle = true, albumIdx = 5))
                    insert(Song("소우주", "방탄소년단 (BTS)",
                        R.drawable.img_album_exp4,0, 180, false, "music_boywithluv", albumIdx = 5))
                    insert(Song("ISLAND", "위너 (WINNER)",
                        R.drawable.img_album_exp9,0, 180, false, "music_island", isTitle = true, albumIdx = 6))
                    insert(Song("LOVE ME LOVE ME", "위너 (WINNER)",
                        R.drawable.img_album_exp9,0, 180, false, "music_island", isTitle = true, albumIdx = 6))
                    insert(Song("TOMBOY", "(여자)아이들",
                        R.drawable.img_album_exp8,0, 180, false, "music_tomboy", isTitle = true, albumIdx = 7))
                    insert(Song("Butter", "방탄소년단 (BTS)",
                        R.drawable.img_album_exp,0, 180, false, "music_butter", isTitle = true, albumIdx = 8))
                    insert(Song("Weekend", "태연 (Tae Yeon)",
                        R.drawable.img_album_exp6,0, 180, false, "music_weekend", isTitle = true, albumIdx = 10))
                    insert(Song("뿜뿜", "모모랜드 (MOMOLANDS)",
                        R.drawable.img_album_exp5,0, 180, false, "music_bboom", isTitle = true, albumIdx = 9))
                }

                // song 정보를 다시 넣어줌
                songs = songDB.songDao().getSongsInAlbum(1) as ArrayList<Song>

                // 데이터가 잘 들어왔는지 확인
//        val _songs = songDB.songDao().getSongs()
                Log.d("DB Song data", songs.toString())
            }.start()
        }
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
                        //TODO: 재생 시간 종료 시 동작 정의
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
//                Log.d("Main", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}