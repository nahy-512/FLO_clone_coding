package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemLockerBinding

class LockerRVAdapter(private val songList: ArrayList<Song>): RecyclerView.Adapter<LockerRVAdapter.ViewHolder>() {
    interface MyItemClickListener {
        fun onRemoveSong(position: Int) // 삭제 진행
        fun onChangePlayState(position: Int) // 정지 or 재생 버튼
    }

    private lateinit var mItemClickListener: MyItemClickListener

    // 아이템의 재생 상태를 저장하는 리스트
    private var songStatusList = MutableList<Boolean>(songList.size) { false }


    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun removeItem(position: Int) {
        songList.removeAt(position)
        songStatusList.removeAt(position)
        notifyDataSetChanged()
//        notifyItemRemoved(position)
//        notifyItemRangeRemoved(position, songList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LockerRVAdapter.ViewHolder {
        val binding: ItemLockerBinding = ItemLockerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LockerRVAdapter.ViewHolder, position: Int) {
        holder.bind(songList[position])

        holder.apply {
            // 재생 버튼 클릭 -> 정지 버튼으로 바꾸기
            binding.itemLockerPlayIv.setOnClickListener {
                val isPlaying = songStatusList[position]
                // 클릭한 아이템 상태 변경
                songStatusList[position] = !isPlaying
                if (isPlaying) {
                    binding.itemLockerPlayIv.setImageResource(R.drawable.btn_miniplayer_play)
                } else {
                    binding.itemLockerPlayIv.setImageResource(R.drawable.btn_miniplay_pause)
                }
                mItemClickListener.onChangePlayState(position)
            }

            // 더보기 버튼 클릭 -> 삭제 진행
            binding.itemLockerMoreIv.setOnClickListener { mItemClickListener.onRemoveSong(position) }

            // 재생 버튼 상태 설정
            val isPlaying = songStatusList[position]
            if (isPlaying) {
                binding.itemLockerPlayIv.setImageResource(R.drawable.btn_miniplay_pause)
            } else {
                binding.itemLockerPlayIv.setImageResource(R.drawable.btn_miniplayer_play)
            }
        }
    }

    override fun getItemCount(): Int = songList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class ViewHolder(val binding: ItemLockerBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.itemAlbumTitleTv.text = song.title
            binding.itemLockerSingerTv.text = song.singer
            song.coverImg?.let { binding.itemLockerCoverImgIv.setImageResource(it) }
        }
    }
}