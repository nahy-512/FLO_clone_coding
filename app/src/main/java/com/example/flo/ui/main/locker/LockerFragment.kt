package com.example.flo.ui.main.locker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerBinding
import com.example.flo.ui.main.locker.adapter.LockerVPAdapter
import com.example.flo.ui.signin.LoginActivity
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding

    private val information = arrayListOf("저장한 곡", "음악파일", "저장 앨범")

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

        if (jwt == "") { // 로그인을 헤야하는 상황
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
        spf!!.edit()
            .remove("jwt")
            .remove("userIdx")
            .apply()
    }

    private fun getJwt(): String? {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getString("jwt", "")
    }
}