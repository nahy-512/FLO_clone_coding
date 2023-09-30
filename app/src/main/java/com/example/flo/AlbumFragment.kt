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
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator

class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        receiveHomeData()

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commitAllowingStateLoss()
        }

        val albumAdapter = AlbumVPAdapter(this)
        binding.albumContentVp.adapter = albumAdapter
        // 탭 레이아웃을 뷰페이저와 동기화
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
            tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }

    private fun receiveHomeData() {
        // 번들로부터 ByteArray 받아오기
        val byteArray = arguments?.getByteArray("img")

        if (byteArray != null) {
            // ByteArray를 Bitmap으로 변환
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            // 앨범 커버 이미지에 Bitmap 설정
            binding.albumCoverImgIv.setImageBitmap(bitmap)
        }
//        arguments?.getInt("img")?.let { binding.albumCoverImgIv.setImageResource(it) }
        // 앨범 제목, 가수 설정
        binding.albumTitleTv.text = arguments?.getString("title")
        binding.albumSingerTv.text = arguments?.getString("singer")
    }
}