package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()

    var currentPosition = 0

    // 핸들러 설정
    val handler = Handler(Looper.getMainLooper()){
        // ui 변경하기
        setPage()
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initAlbumRV()

        setPanelAdapter()
        setBannerAdapter()

        return binding.root
    }

    private fun initAlbumRV() {
        // 데이터 리스트 생성 더미 데이터
        albumDatas.apply {
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Album("BB0om BBoom", "모모랜드 (MOMOLANS)", R.drawable.img_album_exp5))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
        }

        // 어댑터와 데이터 리스트 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        // 리사이클러뷰에 어댑터 연결
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        // 레이아웃 매니저 설정
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        /* 아이템 클릭 이벤트 */
        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            override fun onItemClick(album: Album) {
                // 앨범 프레그먼트로 전환
                moveToAlbumFragment(album)
            }
        })
    }

    private fun moveToAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    private fun setPanelAdapter() {
        val viewpager = binding.homePanelImgVp
        val panelAdapter = PanelVPAdapter(this)
        /* 데이터 집어넣기 */
        panelAdapter.addFragment(PanelFragment(R.drawable.img_first_album_default))
        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp4))
        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp6))
        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp8))
        // 뷰페이저와 어댑터 연결
        viewpager.adapter = panelAdapter
        // 뷰페이저 좌우 스크롤 지정
        viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        // 인디케이터 세팅
        val indicator = binding.homePanelIndicator
        indicator.setViewPager(viewpager)
        indicator.createIndicators(4,0)
        indicator.animatePageSelected(2)

        // 뷰페이저 넘기는 쓰레드
        val thread=Thread(PagerRunnable())
        thread.start()
    }


    // 페이지 변경하기
    private fun setPage(){
        // 마지막 페이지면 처음으로 다시 돌아가도록
        if (currentPosition == 4) currentPosition = 0
        binding.homePanelImgVp.setCurrentItem(currentPosition,true)
        currentPosition += 1
    }

    // 3초마다 페이지 넘기기
    inner class PagerRunnable : Runnable{
        override fun run() {
            while(true) {
                try {
                    Thread.sleep(3000)
                    handler.sendEmptyMessage(0)
                } catch (e : InterruptedException){
                    Log.d("HomeFragment", "interupt 발생")
                }
            }
        }
    }

    private fun setBannerAdapter() {
        val bannerAdapter = BannerVPAdater(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        // 뷰페이저와 어댑터 연결
        binding.homeBannerVp.adapter = bannerAdapter
        // 뷰페이저 좌우 스크롤 지정
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }
}