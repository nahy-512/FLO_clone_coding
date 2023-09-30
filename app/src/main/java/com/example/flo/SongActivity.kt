package com.example.flo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding

    private var isRepeat: Boolean = false
    private var isRandom: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongBinding.inflate(layoutInflater)

        setContentView(binding.root)

        onClickListener()

        // title과 singer라는 키 값을 가진 intent가 존재하는지 판별
        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            binding.songTitleTv.text = intent.getStringExtra("title")
            binding.songSingerTv.text = intent.getStringExtra("singer")
        }
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

    private fun sendAlbumTitle() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.STRING_INTENT_KEY, binding.songTitleTv.text)
        }
        setResult(Activity.RESULT_OK, intent)
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
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
}