package com.example.flo

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding

    private var timer: Timer? = null
    private var mediaPlayer: MediaPlayer? = null

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 1

    private var repeatState: Int = 0
    private var isRandom: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()
    }

    // 사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause() {
        super.onPause()

        // 곡 재생 정보 저장
        savePlayingData()
        // 음악 재생 중지
        setPlayerStatus(false)
    }

    override fun onDestroy() {
        super.onDestroy()

        /* 스레드 종료 및 리소스 해제 */
        timer?.interrupt()
        mediaPlayer?.release() // 미디어플레이어가 가지고 있던 리소스 해제
        mediaPlayer = null // 미디어 플레이어 해제
    }

    private fun initClickListener() {
        /* 뒤로가기 버튼 */
        binding.songDownIv.setOnClickListener {
            sendAlbumTitle()
            finish()
        }

        /* 재생 버튼 */
        binding.songPlayerPlayIv.setOnClickListener {
            setPlayerStatus(true)
        }
        /* 정지 버튼 */
        binding.songPlayerPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }
        /* 이전 재생 버튼 */
        binding.songPlayerPrevIv.setOnClickListener {
            moveSong(-1)
        }
        /* 다음 재생 버튼 */
        binding.songPlayerNextIv.setOnClickListener {
            moveSong(+1)
        }

        /* 반복 재생 버튼 */
        binding.songPlayerRepeatIv.setOnClickListener {
            setRepeatStatus(repeatState)
        }

        /* 전체 재생 버튼 */
        binding.songPlayerRandomIv.setOnClickListener {
            setRandomStatus(!isRandom)
        }

        /* 좋아요 버튼 */
        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }
    }

    private fun initPlayList() {
        // DB의 song 리스트를 뽑아서 songs에 저장
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun savePlayingData() {
        //TODO: 노래 재생 시간 DB에 업데이트
        val second = ((binding.songPlayProgressSb.progress * songs[nowPos].playTime)/100)/1000 // 재생 시간을 초 단위로 변환
        // 곡 재생 정보 업데이트
        songDB.songDao().updatePlayingStateById(second, songs[nowPos].isPlaying, songs[nowPos].id)
        Log.d("onPause()", "현재 재생 시간: ${second}")

        // 어플이 종료해도 데이터가 남아있을 수 있도록 마지막으로 재생한 곡의 id를 내부 저장소에 저장
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터
        editor.putInt("songId", songs[nowPos].id)
        editor.apply()
    }

    fun serviceStart(view: View) {
        val intent = Intent(this, Foreground::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    fun serviceStop(view: View) {
        val intent = Intent(this, Foreground::class.java)
        stopService(intent)
    }

    private fun initSong() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        // 현재 곡 위치 계산하기
        nowPos = getPlayerSongPosition(songId)

        Log.d("now Song ID", songs[nowPos].id.toString())

        // song 정보로 UI 업데이트
        setPlayer(songs[nowPos])
    }

    private fun setLike(isLike: Boolean) {
        songs[nowPos].isLike = !isLike
        // DB의 값 업데이트
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)
        // 뷰 랜더링
        if (songs[nowPos].isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
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

    private fun getPlayerSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song: Song) {
        // 음악 파일을 찾아서 넣어줌
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        // 재생 버튼 및 음악 재생 상태 지정
        setPlayerStatus(song.isPlaying)

        // 이전 재생 시간 반영
        mediaPlayer?.seekTo(song.second * 1000)
        // 음악 종료 시간 설정
        song.playTime = mediaPlayer?.duration!! / 1000

        // 위젯 반영
        with (binding) {
            songTitleTv.text = song.title
            songSingerTv.text = song.singer
            song.coverImg?.let { songCoverImgIv.setImageResource(it) }
            songPlayStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
            songPlayEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
            songPlayProgressSb.progress = song.second * 100000 / song.playTime
            // 좋아요
            if (song.isLike) {
                binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            } else {
                binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            }
        }

        // 타이머 시작
        startTimer(song.playTime)
    }

    private fun sendAlbumTitle() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.STRING_INTENT_KEY, "${binding.songTitleTv.text}_${binding.songSingerTv.text}")
        }
        setResult(Activity.RESULT_OK, intent)
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
        timer?.isPlaying = isPlaying

        if (isPlaying) { // 재생 상태
            binding.songPlayerPlayIv.visibility = View.GONE
            binding.songPlayerPauseIv.visibility = View.VISIBLE
            // 음악 재생
            mediaPlayer?.start()
            // 알림창 띄우기
//            serviceStart(binding.songPlayerPlayIv)
        } else { // 정지 상태
            binding.songPlayerPlayIv.visibility = View.VISIBLE
            binding.songPlayerPauseIv.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) { // 음악 재생 중일 떄
                // 음악 중지
                mediaPlayer?.pause()
            }
            // 알림창 내리기
//            serviceStop(binding.songPlayerPauseIv)
        }
    }

    private fun setRepeatStatus(repeatState: Int) {
        this.repeatState = (repeatState + 1) % 3
        when (this.repeatState) {
            0 -> binding.songPlayerRepeatIv.setImageResource(R.drawable.nugu_btn_repeat_inactive) // 반복 X
            1 -> binding.songPlayerRepeatIv.setImageResource(R.drawable.btn_playlist_repeat_on1) // 한곡 재생
            else -> binding.songPlayerRepeatIv.setImageResource(R.drawable.btn_player_repeat_on_light) // 반복 재생
        }
    }

    private fun setRandomStatus(isRandom: Boolean) {
        if (isRandom) { // 반복 상태
            binding.songPlayerRandomIv.setImageResource(R.drawable.btn_player_random_on_light)
        } else {
            binding.songPlayerRandomIv.setImageResource(R.drawable.nugu_btn_random_inactive)
        }
        this.isRandom = isRandom
    }

    private fun startTimer(playTime: Int) {
        timer = Timer(playTime, songs[nowPos].isPlaying)
        timer?.start()
    }

    private fun initializeMusic(isPlaying: Boolean) {
        // 리소스 해제
        timer?.interrupt()
        mediaPlayer?.release() // 미디어플레이어가 가지고 있던 리소스 해제
        mediaPlayer = null // 미디어 플레이어 해제
        // 재생 시간 초기화
        songs[nowPos].second  = 0
        songs[nowPos].isPlaying = isPlaying
        runOnUiThread {
            setPlayerStatus(isPlaying)
            binding.songPlayProgressSb.progress = songs[nowPos].second
            binding.songPlayStartTimeTv.text = String.format("%02d:%02d", songs[nowPos].second.div(60), songs[nowPos].second.rem(60))
            // 새로운 노래 재시작 준비
            setPlayer(songs[nowPos])
        }
        // 스레드 종료
        Thread.sleep(50)
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
                        when (repeatState) {
                            1 -> initializeMusic(true) // 한곡 재생 상태에서는 바로 초기화해서 다시 재생
                            else -> initializeMusic(false) // 그 이외의 경우에는 음악 재생 중지
                        }
                        break
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        // 시간이 증가했으므로 seekbar에도 적용
                        runOnUiThread {
                            binding.songPlayProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        // 진행 시간 표시 타이머
                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                binding.songPlayStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
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