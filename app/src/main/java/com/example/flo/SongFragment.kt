package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSongBinding

class SongFragment : Fragment() {

    lateinit var binding: FragmentSongBinding

    lateinit var songDB: SongDatabase

    private lateinit var trackRVAdapter: TrackRVAdapter

    private var albumId: Int = 1
    private var isMix: Boolean = false
    
    companion object {
        fun newInstance(albumId: Int): SongFragment {
            val fragment = SongFragment()
            fragment.albumId = albumId
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater, container, false)

        binding.songMyFavMixToggleIv.setOnClickListener {
            setMixStatus(!isMix)
        }

        initTrackRV()

        return binding.root
    }

    private fun initTrackRV() {

        Log.d("SongFragment", "albumId: $albumId")

        songDB = SongDatabase.getInstance(requireContext())!!

        trackRVAdapter = TrackRVAdapter()
        binding.songTrackRv.adapter = trackRVAdapter
        binding.songTrackRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 수록곡 목록 추가
        trackRVAdapter.addSongs(songDB.songDao().getSongsInAlbum(albumId) as ArrayList<Song>)
    }

    private fun setMixStatus(isMix: Boolean) {
        if (isMix) { // 내 취향 믹스
            binding.songMyFavMixToggleIv.setImageResource(R.drawable.btn_toggle_on)
        } else {
            binding.songMyFavMixToggleIv.setImageResource(R.drawable.btn_toggle_off)
        }
        this.isMix = isMix
    }
}