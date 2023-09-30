package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdater(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // 여러 개의 프래그먼트를 담아둘 하나의 리스트 선언
    private val fragmentList: ArrayList<Fragment> = ArrayList()

    // 프래그먼트 사이즈만큼 아이템을 지정
    override fun getItemCount(): Int = fragmentList.size

    // 프래그먼트들을 생성
    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun addFragment(fragment: Fragment) {
        // 프래그먼트 리스트에 인자값으로 받은 프래그먼트를 추가
        fragmentList.add(fragment)
        // 뷰페이저에게 새로운 값이 리스트에 추가됐음을 알림
        notifyItemInserted(fragmentList.size - 1)
    }
}