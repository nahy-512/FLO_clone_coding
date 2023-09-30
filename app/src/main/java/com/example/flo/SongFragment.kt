package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentDetailBinding
import com.example.flo.databinding.FragmentSongBinding

class SongFragment : Fragment() {

    lateinit var binding: FragmentSongBinding

    private var isMix: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater, container, false)

        binding.songMyFavMixToggleIv.setOnClickListener {
            setMixStatus(!isMix)
        }

        return binding.root
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