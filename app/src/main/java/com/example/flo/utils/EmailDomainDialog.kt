package com.example.flo.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.flo.databinding.DialogEmailDomainBinding

class EmailDomainDialog: DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogEmailDomainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEmailDomainBinding.inflate(inflater, container, false)

        initClickListeners()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initClickListeners() {
        binding.domainCloseTv.setOnClickListener {
            dismiss()
        }
    }
}