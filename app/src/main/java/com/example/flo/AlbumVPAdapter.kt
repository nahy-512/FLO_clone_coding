package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumVPAdapter(fragment: Fragment, private val albumId: Int, private val isRoomData: Boolean) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> SongFragment.newInstance(albumId, isRoomData)     // 수록곡
            1 -> DetailFragment()   // 상세정보
            else -> VideoFragment() // 영상
        }
    }
}