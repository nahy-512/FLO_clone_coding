package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLockerSaveBinding

class SavedSongFragment: Fragment() {

    lateinit var binding : FragmentLockerSaveBinding

    lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSaveBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initSongRv()
    }

    private fun initSongRv() {

        songDB = SongDatabase.getInstance(requireContext())!!

        val songRVAdapter = SavedSongRVAdapter()
        binding.lockerSaveSongRv.adapter = songRVAdapter
        binding.lockerSaveSongRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 좋아요한 곡 목록을 추가
        songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)

        // 버튼 클릭 시 좋아요 여부 DB 업데이트
        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
//                songRVAdapter.removeItem(position)
            }
        })
    }
}