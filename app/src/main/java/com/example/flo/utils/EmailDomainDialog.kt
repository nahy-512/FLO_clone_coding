package com.example.flo.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.example.flo.databinding.DialogEmailDomainBinding

class EmailDomainDialog: DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogEmailDomainBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemClickListener: EmailDomainClickInterface

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
        binding.emailDomainCloseTv.setOnClickListener {
            dismiss()
        }

        binding.emailDomainRadioGroup.setOnCheckedChangeListener { group, checkedId ->
//            val selectedRadioButton = view?.findViewById<RadioButton>(checkedId)
            val selectedRadioButton: RadioButton? = view?.findViewById(checkedId)

            // selectedRadioButton이 null이 아니면 해당 라디오 버튼의 텍스트를 가져와서 표시
            selectedRadioButton?.let {
//                r1Result.text = "오늘 저녁은? ${it.text}!"
                itemClickListener.onRadioButton(it.text as String)
                dismiss()
            }
        }
    }

    interface EmailDomainClickInterface {
        fun onRadioButton(domain: String)
    }

    fun setItemClickListener(itemClickListener: EmailDomainClickInterface) {
        this.itemClickListener = itemClickListener
    }
}