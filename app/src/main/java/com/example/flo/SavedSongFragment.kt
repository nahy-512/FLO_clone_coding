package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLockerSavedsongBinding

class SavedSongFragment: Fragment(), EditBarDialogInterface {

    lateinit var binding : FragmentLockerSavedsongBinding

    lateinit var songDB: SongDatabase

    private lateinit var songRVAdapter: SavedSongRVAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)

        initClickListener()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initSongRv()
    }

    private fun initClickListener() {
        /* 전제 선택 버튼 */
        binding.lockerSongSelectAllTv.setOnClickListener {
            // 선택 표시
            changeSelectWidget(true)
            // 바텀시트 띄우기
            showBottomSheetDialog()
        }
    }

    private fun changeSelectWidget(isSelected: Boolean) {
        with(binding) {
            if (isSelected) {
                lockerSongSelectAllTv.text = "선택해제"
                lockerSongSelectAllIv.setImageResource(R.drawable.btn_playlist_select_on)
                lockerSongSelectAllTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.select_color))
            } else {
                lockerSongSelectAllTv.text = "전체선택"
                lockerSongSelectAllIv.setImageResource(R.drawable.btn_playlist_select_off)
                lockerSongSelectAllTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_gray_color))
            }
        }
    }

    private fun initSongRv() {

        songDB = SongDatabase.getInstance(requireContext())!!

        songRVAdapter = SavedSongRVAdapter()
        binding.lockerSongSongRv.adapter = songRVAdapter
        binding.lockerSongSongRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 좋아요한 곡 목록을 추가
        songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)

        // 버튼 클릭 시 좋아요 여부 DB 업데이트
        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
//                songRVAdapter.removeItem(position)
            }
        })
    }

    private fun showBottomSheetDialog() {
        //TODO: 다이얼로그 나올 때 미니플레이어 내려가게 하기
        val dialog = EditbarDialog(this)
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }

    private fun deleteAllSavedSongs() {
        // 좋아요한 모든 곡의 isLike를 false로 업데이트
        songDB.songDao().removeAllLikedSongs()
        // 리사뷰에도 반영
        songRVAdapter.deleteAllList()
    }

    override fun onClickButton(id: Int) {
        // 선택 표시 해제
        changeSelectWidget(false)
        when (id) {
            4 -> { // 삭제
//                Toast.makeText(requireContext(),"삭제 버튼 클릭", Toast.LENGTH_SHORT).show()
                // 저장한 곡 리스트 전체 삭제
                deleteAllSavedSongs()
            }
        }
    }
}