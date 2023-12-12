package com.example.flo.ui.main.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.data.entities.Album
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.FragmentLockerSavedalbumBinding
import com.example.flo.ui.main.locker.adapter.SavedAlbumRVAdapter

class SavedAlbumFragment: Fragment() {

    lateinit var binding : FragmentLockerSavedalbumBinding

    lateinit var albumDB: SongDatabase

    private lateinit var albumRVAdapter: SavedAlbumRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedalbumBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initSongRv()
    }

    private fun initSongRv() {

        val userId: Int = getJwt()

        albumDB = SongDatabase.getInstance(requireContext())!!

        albumRVAdapter = SavedAlbumRVAdapter()
        binding.lockerAlbumAlbumRv.adapter = albumRVAdapter
        binding.lockerAlbumAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 좋아요한 앨범 목록을 추가
        albumRVAdapter.addAlbums(albumDB.albumDao().getUserLikedAlbums(userId) as ArrayList<Album>)

        // 버튼 클릭 시 좋아요 여부 DB 업데이트
        albumRVAdapter.setMyItemClickListener(object : SavedAlbumRVAdapter.MyItemClickListener {
            override fun onRemoveSong(albumId: Int) {
                albumDB.albumDao().dislikedAlbum(userId, albumId)
            }
        })
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("userIdx", 0)
    }
}