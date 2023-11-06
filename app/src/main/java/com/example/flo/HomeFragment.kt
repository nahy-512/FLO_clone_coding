package com.example.flo

import android.content.Context
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

interface AlbumClickListener {
    fun onAlbumReceived(albumIdx: Int)
}

class HomeFragment : Fragment() {

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

        inputDummyAlbums()
        initAlbumRV()
        setPanelAdapter()
        setBannerAdapter()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AlbumClickListener) {
            listner = context
        } else {
            throw RuntimeException("$context must implement AlbumClickListener")
        }
    }

    private fun inputDummyAlbums() {
        // DB로부터 album 데이터를 모두 조회해옴
        albums = albumDB.albumDao().getAlbums() as ArrayList<Album>

        // 저장된 albums 데이터가 있다면 바로 리턴
        if (albums.isNotEmpty()) return

        // albums가 비어있다면 더미데이터를 넣어줌
        albumDB.albumDao().insert(
            Album("IU 5th Album 'LILAC'", "아이유 (IU)", R.drawable.img_album_exp2)
        )
        albumDB.albumDao().insert(
            Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3)
        )
        albumDB.albumDao().insert(
            Album("항해", "악뮤 (AKMU)", R.drawable.img_album_exp7)
        )
        albumDB.albumDao().insert(
            Album("Love Poem", "아이유 (IU)", R.drawable.img_album_exp10)
        )
        albumDB.albumDao().insert(
            Album("Map of the Soul", "방탄소년단 (BTS)", R.drawable.img_album_exp4)
        )
        albumDB.albumDao().insert(
            Album("OUR TWENTY FOR", "위너 (WINNER)", R.drawable.img_album_exp9)
        )
        albumDB.albumDao().insert(
            Album("I NEVER DIE", "(여자) 아이들", R.drawable.img_album_exp8)
        )
        albumDB.albumDao().insert(
            Album("Map of the Soul", "방탄소년단 (BTS)", R.drawable.img_album_exp)
        )
        albumDB.albumDao().insert(
            Album("Great!", "모모랜드 (MOMOLANDS)", R.drawable.img_album_exp5)
        )

        // 다시 데이터를 넣어줌
        albums = albumDB.albumDao().getAlbums() as ArrayList<Album>

        // 데이터가 잘 들어왔는지 확인
        val _albums = albumDB.albumDao().getAlbums()
        Log.d("DB data", _albums.toString())
    }


    private fun initAlbumRV() {

        // 어댑터와 데이터 리스트 연결
        val albumRVAdapter = AlbumRVAdapter(albums)
        // 리사이클러뷰에 어댑터 연결
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        // 레이아웃 매니저 설정
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        /* 아이템 클릭 이벤트 */
        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            // 아이템 전체 클릭
            override fun onItemClick(album: Album) {
                // 앨범 프레그먼트로 전환
                moveToAlbumFragment(album)
            }
            // 아이템 재생 버튼 클릭
            override fun onPlayBtnClick(albumIdx: Int) {
                // 메인 액티비티로 재생할 앨범 데이터를 전달
                listner?.onAlbumReceived(albumIdx)
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