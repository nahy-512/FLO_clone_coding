package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLockerSaveBinding

class LockerSaveFragment: Fragment() {

    lateinit var binding : FragmentLockerSaveBinding
    private var songDatas = ArrayList<Song>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSaveBinding.inflate(inflater, container, false)

        initSongRv()

        return binding.root
    }

    private fun initSongRv() {
        songDatas.apply {
            add(Song("LILAC", "아이유 (IU)", R.drawable.img_album_exp2,0, 60, false, "music_lilac"))
            add(Song("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3,0, 60, false, "music_next"))
            add(Song("달", "악뮤 (AKMU)", R.drawable.img_album_exp7,0, 60, false, "music_moon"))
            add(Song("Blueming", "아이유 (IU)", R.drawable.img_album_exp10,0, 60, false, "music_blueming"))
            add(Song("flu", "아이유 (IU)", R.drawable.img_album_exp2,0, 60, false, "music_flu"))
            add(Song("작은 것들을 위한 시", "방탄소년단 (BTS)", R.drawable.img_album_exp4,0, 60, false, "music_butter"))
            add(Song("Island", "위너 (WINNER)", R.drawable.img_album_exp9,0, 60, false, "music_island"))
            add(Song("뿜뿜", "모모랜드 (MOMOLANDS)", R.drawable.img_album_exp5,0, 60, false, "music_bboom"))
            add(Song("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6,0, 60, false, "music_lilac"))
            add(Song("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp,0, 60, false, "music_butter"))
            add(Song("TOMBOY", "(여자)아이들", R.drawable.img_album_exp8,0, 60, false, "music_tomboy"))
        }

        val lockerRVAdapter = LockerRVAdapter(songDatas)
        binding.lockerSaveSongRv.adapter = lockerRVAdapter
        binding.lockerSaveSongRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        lockerRVAdapter.setMyItemClickListener(object : LockerRVAdapter.MyItemClickListener {
            override fun onRemoveSong(position: Int) {
               lockerRVAdapter.removeItem(position)
            }

            override fun onChangePlayState(position: Int) {
                //
            }
        })
    }
}