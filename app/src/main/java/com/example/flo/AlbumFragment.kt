package com.example.flo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding
    private var gson: Gson = Gson()

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var albumId: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        receiveHomeData()

        binding.albumBackIv.setOnClickListener {
            val fragmentManager = (context as MainActivity).supportFragmentManager

            // 이전의 모든 프래그먼트를 백 스택에서 제거 (HomeFragment)
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val albumAdapter = AlbumVPAdapter(this, albumId)
        binding.albumContentVp.adapter = albumAdapter
        // 탭 레이아웃을 뷰페이저와 동기화
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
                tab, position ->
            tab.text = information[position]
        }.attach()
    }

    private fun receiveHomeData() {
        // argument에서 데이터를 꺼내기
        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)
        albumId = album.id
        Log.d("AlbumFragment", "albumId: $albumId")
        // 바인딩
        setInit(album)
    }

    private fun setInit(album: Album) {
        // 앨범 제목, 가수, 이미지 설정
        binding.albumTitleTv.text = album.title
        binding.albumSingerTv.text = album.singer
        album.coverImg?.let { binding.albumCoverImgIv.setImageResource(it) }
    }
}