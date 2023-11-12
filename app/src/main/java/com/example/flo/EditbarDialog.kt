package com.example.flo

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //TODO: 등장할 때 자연스러운 애니메이션으로 나오도록 하기

        // 배경은 투명하게 하기
        val dialog = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogTheme).apply {
            // 배경 클릭 시의 동작을 여기에 추가
            setOnShowListener {
                view?.setOnClickListener {
                    // 배경 클릭 시에 원하는 동작 수행
                    editBarDialogInstance?.onClickButton(0)
                    dismiss()
                }
            }
//            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        return dialog
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

    private fun initClickListener() {
        with (binding) {
            // 1: 듣기, 2: 재생 목록, 3: 내 리스트, 4: 삭제하기
            val layoutList = listOf(editbarPlayLl, editbarAddplaylistLl, editbarMylistLl, editbarDeleteLl)

            for (i: Int in layoutList.indices) {
                layoutList[i].setOnClickListener {
                    editBarDialogInstance?.onClickButton(i + 1)
                    // 창 닫기
                    dismiss()
                }
            }
        }
    }
}