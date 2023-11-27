package com.example.flo.ui.main.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.R
import com.example.flo.data.entities.Album
import com.example.flo.data.local.SongDatabase
import com.example.flo.data.remote.AlbumResult
import com.example.flo.data.remote.AlbumService
import com.example.flo.data.remote.Albums
import com.example.flo.databinding.FragmentHomeBinding
import com.example.flo.ui.main.MainActivity
import com.example.flo.ui.main.album.AlbumFragment
import com.google.gson.Gson

interface AlbumClickListener {
    fun onAlbumReceived(albumIdx: Int)
}

class HomeFragment : Fragment(), HomeView {

    lateinit var binding: FragmentHomeBinding

    private var albums = arrayListOf<Album>()
    lateinit var albumDB: SongDatabase

    private var listner: AlbumClickListener? = null

    // 현재 패널 뷰페이저 위치를 저장할 변수
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

        albumDB = SongDatabase.getInstance(requireContext())!!

//        inputDummyAlbums()
        initAlbumRV()
        setPanelAdapter()
        setBannerAdapter()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        getAlbums()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AlbumClickListener) {
            listner = context
        } else {
            throw RuntimeException("$context must implement AlbumClickListener")
        }
    }

    private fun initAlbumRV() {
        // DB의 앨범 정보를 불러옴
        albums = albumDB.albumDao().getAllAlbums() as ArrayList<Album>

        // 어댑터와 데이터 리스트 연결
        val albumRVAdapter = AlbumRVAdapter(albums)
        // 리사이클러뷰에 어댑터 연결
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        // 레이아웃 매니저 설정
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumDB.albumDao().getAlbumsLiveData().observe(viewLifecycleOwner, Observer { albums ->
            if (albums.isNotEmpty()) {
                albumRVAdapter.updateAlbums(albums)
            }
        })

        /* 아이템 클릭 이벤트 */
        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            // 아이템 전체 클릭
            override fun onItemClick(album: Album) {
                // 앨범 프레그먼트로 전환
                moveToAlbumFragment(album, true)
            }
            // 아이템 재생 버튼 클릭
            override fun onPlayBtnClick(albumIdx: Int) {
                // 메인 액티비티로 재생할 앨범 데이터를 전달
                listner?.onAlbumReceived(albumIdx)
            }
        })
    }

    private fun getAlbums() {
        val albumService = AlbumService()
        albumService.setHomeView(this)
        // 앨범 정보를 받아옴
        albumService.getAlbums()
    }

    private fun initPodcastRV(result: AlbumResult) {
        val podAdapter = AlbumPodcastRVAdapter(requireContext(), result)

        binding.homePodcastRv.adapter = podAdapter
        binding.homePodcastRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        /* 아이템 클릭 이벤트 */
        podAdapter.setMyItemClickListener(object : AlbumPodcastRVAdapter.MyItemClickListener {
            // 아이템 전체 클릭
            override fun onItemClick(album: Albums) {
                // 앨범 프레그먼트로 전환
                moveToAlbumFragment(album, false)
            }
        })
    }

    private fun moveToAlbumFragment(album: Any, isRoomDB: Boolean) {
        Log.d("HomeFragment", "album: $album")
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .add(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                    putBoolean("isRoomData", isRoomDB)
                }
            })
            .addToBackStack(null) // 백 스택에 트랜잭션을 추가
            .commitAllowingStateLoss()
    }

    private fun setPanelAdapter() {
        val viewpager = binding.homePanelImgVp
        val panelAdapter = PanelVPAdapter(this)
        /* 데이터 집어넣기 */
        panelAdapter.addFragment(PanelFragment.newInstance(R.drawable.img_first_album_default))
        panelAdapter.addFragment(PanelFragment.newInstance(R.drawable.img_album_exp4))
        panelAdapter.addFragment(PanelFragment.newInstance(R.drawable.img_album_exp6))
        panelAdapter.addFragment(PanelFragment.newInstance(R.drawable.img_album_exp8))
        // 뷰페이저와 어댑터 연결
        viewpager.adapter = panelAdapter
        // 뷰페이저 좌우 스크롤 지정
        viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        // 인디케이터 세팅
        val indicator = binding.homePanelIndicator
        indicator.setViewPager(viewpager)
//        indicator.createIndicators(4,0)
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
                    Log.d("HomeFragment", "interrupt 발생")
                }
            }
        }
    }

    private fun setBannerAdapter() {
        val bannerAdapter = BannerVPAdater(this)
        bannerAdapter.addFragment(BannerFragment.newInstance(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment.newInstance(R.drawable.img_home_viewpager_exp2))
        // 뷰페이저와 어댑터 연결
        binding.homeBannerVp.adapter = bannerAdapter
        // 뷰페이저 좌우 스크롤 지정
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    override fun onGetAlbumsLoading() {
        binding.homeLoadingPb.visibility = View.VISIBLE
    }

    override fun onGetAlbumsSuccess(code: Int, result: AlbumResult) {
        Log.d("HomeFragment", "onGetAlbumsSuccess")
        binding.homeLoadingPb.visibility = View.GONE
        //
        initPodcastRV(result)
    }

    override fun onGetAlbumsFailure(code: Int, message: String) {
        Log.d("HomeFragment", "onGetAlbumsFailure")
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}