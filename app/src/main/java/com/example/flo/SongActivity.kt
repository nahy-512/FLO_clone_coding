package com.example.flo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var song: Song
    lateinit var timer: Timer

    private var isRepeat: Boolean = false
    private var isRandom: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongBinding.inflate(layoutInflater)

        setContentView(binding.root)

        onClickListener()

        initSong()
        setPlayer(song)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
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
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false)
            )
        }
        // 타이머 시작
        startTimer()
    }

    private fun setPlayer(song: Song) {
        binding.songTitleTv.text = intent.getStringExtra("title")
        binding.songSingerTv.text = intent.getStringExtra("singer")
        binding.songPlayStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songPlayEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songPlayProgressSb.progress = (song.second * 1000 / song.playTime)

        setPlayerStatus(song.isPlaying)
    }

    private fun sendAlbumTitle() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.STRING_INTENT_KEY, binding.songTitleTv.text)
        }
        setResult(Activity.RESULT_OK, intent)
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying) { // 재생 상태
            binding.songPlayerPlayIv.visibility = View.GONE
            binding.songPlayerPauseIv.visibility = View.VISIBLE
        } else { // 정지 상태
            binding.songPlayerPlayIv.visibility = View.VISIBLE
            binding.songPlayerPauseIv.visibility = View.GONE
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

    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }

    inner  class Timer(private val playTime: Int, var isPlaying: Boolean = true): Thread() {

        private var second: Int = 0
        private var mills: Float = 0f

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