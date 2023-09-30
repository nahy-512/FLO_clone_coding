package com.example.flo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.songDownIv.setOnClickListener {
            sendAlbumTitle()
            finish()
        }

        binding.songPlayerPlayIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPlayerPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        // title과 singer라는 키 값을 가진 intent가 존재하는지 판별
        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            binding.songTitleTv.text = intent.getStringExtra("title")
            binding.songSingerTv.text = intent.getStringExtra("singer")
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
}