package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding

    private val information = arrayListOf("저장한 곡", "음악파일")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        initClickListener()
        setVPAdapter()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initView()
    }

    private fun initClickListener() {
        /* 로그인 */
        binding.lockerLoginTv.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }

    private fun setVPAdapter() {
        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter
        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) {
                tab, position ->
            tab.text = information[position]
        }.attach()
    }

    private fun initView() {
        // 텍스트를 로그인으로 할지, 로그아웃으로 할지 결정
        val jwt = getJwt()

        if (jwt == 0) { // 로그인을 헤야하는 상황
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        } else {
            binding.lockerLoginTv.text = "로그아웃"
            binding.lockerLoginTv.setOnClickListener {
                // 로그아웃 진행
                logout()
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
    }

    private fun logout() {
        // jwt가 0인 상태로 만들어서 로그아웃
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        // jwt라는 키 값에 저장된 값을 없애줌
        spf!!.edit().remove("jwt").apply()
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt", 0)
    }
}