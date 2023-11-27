package com.example.flo.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentBannerBinding

class BannerFragment : Fragment() {

    lateinit var binding: FragmentBannerBinding

    private var imgRes: Int = 0

    companion object {
        // 팩토리 메서드를 통해 새로운 프래그먼트 인스턴스를 생성
        fun newInstance(imgRes: Int): BannerFragment {
            val fragment = BannerFragment()
            fragment.imgRes = imgRes
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerBinding.inflate(inflater, container, false)

        // 인자값으로 받은 이미지로 이미지뷰의 src 값이 변경
        binding.bannerImageIv.setImageResource(imgRes)

        return binding.root
    }

}