package com.example.flo.ui.main.locker.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flo.ui.main.locker.MusicFileFragment
import com.example.flo.ui.main.locker.SavedAlbumFragment
import com.example.flo.ui.main.locker.SavedSongFragment

class LockerVPAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SavedSongFragment() // 저장한 곡
            1 -> MusicFileFragment() // 음악파일
            else -> SavedAlbumFragment() // 저장 앨범
        }
    }
}