package com.example.flo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import java.io.ByteArrayOutputStream

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

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

        binding.homeAlbum1CoverIv.setOnClickListener {
            moveToAlbumFragment()
        }

        setPanelAdapter()
        setBannerAdapter()

        return binding.root
    }

    private fun moveToAlbumFragment() {
        val albumFragment = AlbumFragment()

        val bundle = Bundle()

        // 이미지뷰의 이미지를 비트맵으로 변환
        val bitmap = (binding.homeAlbum1CoverIv.drawable as BitmapDrawable).bitmap

        // 비트맵을 ByteArray로 직렬화
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // bundle에 데이터 담기
        bundle.putByteArray("img", byteArray)
//        bundle.putInt("img", R.drawable.img_album_exp2)
        bundle.putString("title", binding.homeAlbum1TitleTv.text.toString())
        bundle.putString("singer", binding.homeAlbum1SingerTv.text.toString())
        // albumFragment의 arguments에 데이터를 담은 bundle을 넘겨줌
        albumFragment.arguments = bundle

        (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, albumFragment).commitAllowingStateLoss()
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