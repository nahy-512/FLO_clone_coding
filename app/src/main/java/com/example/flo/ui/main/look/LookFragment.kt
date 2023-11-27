package com.example.flo.ui.main.look

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.ui.main.album.SongRVAdapter
import com.example.flo.data.remote.FloChartResult
import com.example.flo.data.remote.SongService
import com.example.flo.databinding.FragmentLookBinding

class LookFragment : Fragment(), LookView {

    private lateinit var binding: FragmentLookBinding
    private lateinit var floChartAdapter: SongRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLookBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        getSongs()
    }

    private fun initRecyclerView(result: FloChartResult) {
        floChartAdapter = SongRVAdapter(requireContext(), result)

        binding.lookChartRv.adapter = floChartAdapter
        binding.lookChartRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun getSongs() {
        val songService = SongService()
        songService.setLookView(this)
        // 차트 곡 정보를 받아옴
        songService.getSongs()
    }

    override fun onGetSongsLoading() {
        binding.lookLoadingPb.visibility = View.VISIBLE
    }

    override fun onGetSongsSuccess(code: Int, result: FloChartResult) {
        Log.d("LookFragment", "onGetSongsSuccess")
        // API 연결을 성공하면 로딩창을 없애줌
        binding.lookLoadingPb.visibility = View.GONE
        // 리사이클러뷰 처리
        initRecyclerView(result)
    }

    override fun onGetSongsFailure(code: Int, message: String) {
        Log.d("LookFragment", "onGetSongFailure")
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}