package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerMusicBinding

class MusicFileFragment: Fragment() {

    lateinit var binding : FragmentLockerMusicBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerMusicBinding.inflate(inflater, container, false)

        return binding.root
    }
}