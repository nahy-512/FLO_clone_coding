package com.example.flo.ui.main.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.flo.R
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Like
import com.example.flo.data.local.SongDatabase
import com.example.flo.data.remote.Albums
import com.example.flo.databinding.FragmentAlbumBinding
import com.example.flo.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment()  {

    lateinit var binding: FragmentAlbumBinding
    private var gson: Gson = Gson()

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var albumId: Int = 1
    private var isRoomData: Boolean = false
    private var isLiked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        // HomeFragment에서 넘어온 데이터를 반영
        receiveHomeData()
        initClickListener()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val albumAdapter = AlbumVPAdapter(this, albumId, isRoomData)
        binding.albumContentVp.adapter = albumAdapter
        // 탭 레이아웃을 뷰페이저와 동기화
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
                tab, position ->
            tab.text = information[position]
        }.attach()
    }

    private fun initClickListener() {
        /* 뒤로가기 */
        binding.albumBackIv.setOnClickListener {
            val fragmentManager = (context as MainActivity).supportFragmentManager

            // 이전의 모든 프래그먼트를 백 스택에서 제거 (HomeFragment)
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        /* 앨범 좋아요 */
        val userId = getJwt()
        binding.albumLikeIv.setOnClickListener {
            if (isLiked) { // 좋아요를 취소하는 상황
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(albumId)
            }
            else { // 좋아요를 누르는 상황
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, albumId)
            }
            isLiked = !isLiked
        }
    }

    private fun receiveHomeData() {
        // argument에서 데이터를 꺼내기
        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)
        isRoomData = arguments?.getBoolean("isRoomData", false) == true
        if (isRoomData) {
            albumId = album.id
        } else {
            val albums = gson.fromJson(albumJson, Albums::class.java)
            albumId = albums.id
        }
        Log.d("AlbumFragment", "isRoomData: ${isRoomData}, albumId: $albumId")

        // 좋아요 초기화
        isLiked = isLikedAlbum(albumId)
        // 바인딩
        setInit(album, isRoomData)
    }

    private fun setInit(album: Album, isRoomData: Boolean) {
        // 앨범 제목, 가수, 이미지 설정
        binding.albumTitleTv.text = album.title
        binding.albumSingerTv.text = album.singer
        if (isRoomData) {
            album.coverImg?.let { binding.albumCoverImgIv.setImageResource(it) }
        } else {
            Glide.with(requireContext()).load(album.coverImgUrl).into(binding.albumCoverImgIv)
        }
        // 앨범 좋아요 설정
        if (isLiked) {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun likeAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId)

        songDB.albumDao().likeAlbum(like)
    }

    private fun isLikedAlbum(albumId: Int): Boolean {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        val likeId: Int? = songDB.albumDao().isLikedAlbum(userId, albumId)

        return likeId != null
    }

    private fun disLikedAlbum(albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        songDB.albumDao().dislikedAlbum(userId, albumId)
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("userIdx", 0)
    }
}