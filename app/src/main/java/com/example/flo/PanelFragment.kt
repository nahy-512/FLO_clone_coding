package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentPanelBinding

class PanelFragment : Fragment() {

    lateinit var binding : FragmentPanelBinding

    private var imgRes: Int = 0

    companion object {
        // 팩토리 메서드를 통해 새로운 프래그먼트 인스턴스를 생성
        fun newInstance(imgRes: Int): PanelFragment {
            val fragment = PanelFragment()
            fragment.imgRes = imgRes
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPanelBinding.inflate(inflater, container, false)

        binding.panelImageIv.setImageResource(imgRes)

        return binding.root
    }
}