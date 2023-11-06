package com.example.flo

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flo.databinding.DialogEditbarBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

interface EditBarDialogInterface {
    fun onClickButton(id: Int)
}

class EditbarDialog(dialogInterface: EditBarDialogInterface,) : BottomSheetDialogFragment() { //var listner: EditBarClickListener

    private lateinit var binding: DialogEditbarBinding
    private var editBarDialogInstance: EditBarDialogInterface? = null

    init {
        this.editBarDialogInstance = dialogInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditbarBinding.inflate(inflater, container, false)

        initClickListener()

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //TODO: 등장할 때 자연스러운 애니메이션으로 나오도록 하기

        // 배경은 투명하게 하기
        val dialog = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogTheme).apply {
//            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        return dialog
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme)
    }


    private fun initClickListener() {
        binding.editbarDeleteLl.setOnClickListener {
            this.editBarDialogInstance?.onClickButton(4) // 삭제
            // 창 닫기
            dismiss()
        }
    }
}