package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSongBinding

class SongFragment : Fragment(), AlbumView {

    lateinit var binding: FragmentSongBinding

    lateinit var songDB: SongDatabase

    private lateinit var trackRVAdapter: TrackRVAdapter
    private val trackList = ArrayList<Track>()

    private var albumId: Int = 1
    private var isRoomData: Boolean = false
    private var isMix: Boolean = false
    
    companion object {
        fun newInstance(albumId: Int, isRoomData: Boolean): SongFragment {
            val fragment = SongFragment()
            fragment.albumId = albumId
            fragment.isRoomData = isRoomData
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

        Log.d("SongFragment", "isRoomData: ${isRoomData}, albumId: $albumId")

        // 어댑터 정의
        trackRVAdapter = TrackRVAdapter()
        binding.songTrackRv.adapter = trackRVAdapter
        binding.songTrackRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        if (isRoomData) {
            songDB = SongDatabase.getInstance(requireContext())!!
            // DB에서 해당 albumIdx 해당하는 수록곡들을 가져와서 trackList에 저장
            for (albumTrack in songDB.songDao().getSongsInAlbum(albumId)) {
                val track = Track(
                    id = albumTrack.id,
                    title = albumTrack.title,
                    singer = albumTrack.singer,
                    isTitle = albumTrack.isTitle
                )
                trackList.add(track)
            }
            // 수록곡 목록 추가
            trackRVAdapter.addSongs(trackList)
        } else {
            // 서버 데이터 받아옴
            getServerTracks()
        }
    }

    private fun initTrackRV(result: List<AlbumTracks>) {
        for (albumTrack in result) {
            val track = Track(
                id = albumTrack.id,
                title = albumTrack.title,
                singer = albumTrack.singer,
                isTitle = albumTrack.isTitleSong == "T"
            )
            trackList.add(track)
        }
        // 수록곡 목록 추가
        trackRVAdapter.addSongs(trackList)
    }


    private fun getServerTracks() {
        Log.e("SongFragment", "albumIdx: $albumId")
        val albumService = AlbumService()
        albumService.setAlbumView(this)
        // 앨범 수록곡 정보를 받아옴
        albumService.getAlbumTracks(albumId)
    }

    private fun setMixStatus(isMix: Boolean) {
        if (isMix) { // 내 취향 믹스
            binding.songMyFavMixToggleIv.setImageResource(R.drawable.btn_toggle_on)
        } else {
            binding.songMyFavMixToggleIv.setImageResource(R.drawable.btn_toggle_off)
        }
        this.isMix = isMix
    }


    override fun onGetAlbumTracksLoading() {
        binding.songLoadingPb.visibility = View.VISIBLE
    }

    override fun onGetAlbumTracksSuccess(code: Int, result: List<AlbumTracks>) {
        Log.d("AlbumFragment", "onGetAlbumTracksSuccess")
        binding.songLoadingPb.visibility = View.GONE
        initTrackRV(result)
    }

    override fun onGetAlbumTracksFailure(code: Int, message: String) {
        Log.d("AlbumFragment", "onGetAlbumTracksFailure, $code, $message")
    }
}