package com.example.flo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), AlbumClickListener {

    companion object { const val STRING_INTENT_KEY = "song_key" }

    lateinit var binding: ActivityMainBinding

    private var song: Song = Song()
    private var gson: Gson = Gson()

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

        moveToSongActivity()
        initBottomNavigation()
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = sharedPreferences.getString("songData", null)

        song = if (songJson == null) { // 데이터가 존재하지 않으면 Song 데이터를 직접 넣어줌
            Song("라일락", "아이유(IU)", R.drawable.img_album_exp2,0, 60, false, "music_lilac")
        } else { // 존재하면 저장된 데이터를 넣어줌
            gson.fromJson(songJson, Song::class.java)
        }

        // 미니플레이어에 데이터 반영
        setMiniPlayer(song)
    }

    private fun setMiniPlayer(song: Song) {
        with(binding) {
            mainMiniplayerTitleTv.text = song.title
            mainMiniplayerSingerTv.text = song.singer
            mainMiniplayerProgressSb.progress= (song.second * 100000) / song.playTime
        }
    }

    private fun moveToSongActivity() {
//        val song = Song(binding.mainMiniplayerTitleTv.text.toString(), binding.mainMiniplayerSingerTv.text.toString(), 0, 60, false, "music_lilac")
        Log.d("Song", song.title + song.singer)

        binding.mainPlayerCl.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("coverImg", song.coverImg)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)
//            startActivity(intent)
            // SongActivity에서 돌아올 때 받은 앨범 제목
            getResultText.launch(intent)
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
            song = songData
        } else { // 수록곡 정보가 없으면 앨범 제목과 가수를 넣어줌
            binding.mainMiniplayerTitleTv.text = data.title
            binding.mainMiniplayerSingerTv.text = data.singer
            // song 제목, 가수, 이미지 업데이트
            song.title = data.title.toString()
            song.singer = data.singer.toString()
            song.coverImg = data.coverImg
        }
    }
}