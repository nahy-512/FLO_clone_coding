package com.example.flo

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var song: Song
    private var timer: Timer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    private var isRepeat: Boolean = false
    private var isRandom: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        // 미니플레이어에서 받아온
        initSong()
        setPlayer(song)
        onClickListener()
    }

    // 사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        song.second = ((binding.songPlayProgressSb.progress * song.playTime)/100)/1000 // 재생 시간을 초 단위로 변환

        // 어플이 종료해도 데이터가 남아있을 수 있도록 내부 저장소에 저장
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)
        editor.apply()
//        editor.putString("title", song.title)
//        editor.putString("singer", song.singer) ...
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.interrupt()
        mediaPlayer?.release() // 미디어플레이어가 가지고 있던 리소스 해제
        mediaPlayer = null // 미디어 플레이어 해제
    }

    private fun onClickListener() {
        binding.songDownIv.setOnClickListener {
            sendAlbumTitle()
            finish()
        }

        /* 재생 버튼 */
        binding.songPlayerPlayIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPlayerPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        /* 반복 재생 버튼 */
        binding.songPlayerRepeatIv.setOnClickListener {
            setRepeatStatus(!isRepeat)
        }

        /* 전체 재생 버튼 */
        binding.songPlayerRandomIv.setOnClickListener {
            setRandomStatus(!isRandom)
        }
    }

    private fun initSong() {
        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 60),
                intent.getBooleanExtra("isPlaying", false),
                intent.getStringExtra("music")!!
            )
        }
    }

    private fun setPlayer(song: Song) {
        // 음악 파일을 찾아서 넣어줌
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        // 재생 버튼 및 음악 재생 상태 지정
        setPlayerStatus(song.isPlaying)

        // 음악 종료 시간 설정
        song.playTime = mediaPlayer?.duration!! / 1000

        // 위젯 반영
        with (binding) {
            songTitleTv.text = intent.getStringExtra("title")
            songSingerTv.text = intent.getStringExtra("singer")
            songPlayStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
            songPlayEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
            // 이전 재생 시간 반영
            mediaPlayer?.seekTo(song.second * 1000)
            songPlayProgressSb.progress = song.second * 100000 / song.playTime
        }

        // 타이머 시작
        startTimer(song.playTime)
    }

    private fun sendAlbumTitle() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.STRING_INTENT_KEY, binding.songTitleTv.text)
        }
        setResult(Activity.RESULT_OK, intent)
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        song.isPlaying = isPlaying
        timer?.isPlaying = isPlaying

        if (isPlaying) { // 재생 상태
            binding.songPlayerPlayIv.visibility = View.GONE
            binding.songPlayerPauseIv.visibility = View.VISIBLE
            // 음악 재생
            mediaPlayer?.start()
        } else { // 정지 상태
            binding.songPlayerPlayIv.visibility = View.VISIBLE
            binding.songPlayerPauseIv.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) { // 음악 재생 중일 떄
                // 음악 중지
                mediaPlayer?.pause()
            }
        }
    }

    private fun setRepeatStatus(isRepeat: Boolean) {
        if (isRepeat) { // 반복 상태
            binding.songPlayerRepeatIv.setImageResource(R.drawable.btn_player_repeat_on_light)
        } else {
            binding.songPlayerRepeatIv.setImageResource(R.drawable.nugu_btn_repeat_inactive)
        }
        this.isRepeat = isRepeat
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
        timer = Timer(playTime, song.isPlaying)
        timer?.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true): Thread() {

        private var second: Int = song.second // 이전 시작 시간부터 이어서 설정
        private var mills: Float = second.toFloat() * 1000

        override fun run() {
            super.run()
            try {
                // 타이머는 계속 진행되어야 함
                while (true) {
                    if (second >= playTime) { // 노래 재생 시간이 다 끝나면 종료
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
            }catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}