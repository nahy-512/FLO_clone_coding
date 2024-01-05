package com.example.flo.ui.main.look

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.R
import com.example.flo.ui.main.album.adapter.SongRVAdapter
import com.example.flo.data.remote.song.FloChartResult
import com.example.flo.data.remote.song.LookView
import com.example.flo.data.remote.song.SongService
import com.example.flo.databinding.FragmentLookBinding

class LookFragment : Fragment(), LookView {

    private lateinit var binding: FragmentLookBinding
    private lateinit var floChartAdapter: SongRVAdapter

    lateinit var scrollView : ScrollView

    private lateinit var tabList: List<TextView>
    private lateinit var titleList: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLookBinding.inflate(inflater, container, false)

        setInit()
        setTabClickListeners()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        getSongs()
    }

    private fun setInit() {
        binding.apply {
            // 스크롤뷰 초기화
            scrollView = lookSv
            // 탭 리스트 초기화
            tabList = listOf(
                lookTabChart, lookTabVideo, lookTabGenre,
                lookTabSituation, lookTabMood, lookTabAudio
            )
            // 타이틀 리스트 초기화
            titleList = listOf(
                lookChartTv, lookVideoTv, lookGenreTv,
                lookSituationTv, lookMoodTv, lookAudioTv
            )
        }
    }

    private fun setTabClickListeners() {
        for (i in tabList.indices) {
            val button = tabList[i]

            button.setOnClickListener {
                initButton(i)
            }
        }
    }

    private fun initButton(idx : Int) {
        for (presentTab: TextView in tabList) {
            if(presentTab == tabList[idx]) { // 선택된 탭
                presentTab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.select_color))
                presentTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else { // 선택되지 않은 탭
                presentTab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_gray_color))
                presentTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_gray_color))
            }
        }
        // 선택된 탭의 섹션 위치로 이동
        scrollView.smoothScrollTo(0, titleList[idx].top)
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