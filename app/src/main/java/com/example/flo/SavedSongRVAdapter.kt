package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemLockerBinding

class SavedSongRVAdapter(): RecyclerView.Adapter<SavedSongRVAdapter.ViewHolder>() {
    interface MyItemClickListener {
        fun onRemoveSong(songId: Int) // 삭제 진행
//        fun onChangePlayState(songId: Int) // 정지 or 재생 버튼
    }

    private var songs = ArrayList<Song>()
    private lateinit var mItemClickListener: MyItemClickListener

    // 아이템의 재생 상태를 저장하는 리스트
    private lateinit var songStatusList: MutableList<Boolean>

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun addSongs(songs: ArrayList<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)
        this.songStatusList = MutableList(songs.size) { false }

        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        songs.removeAt(position)
        songStatusList.removeAt(position)
        notifyDataSetChanged()
//        notifyItemRemoved(position)
//        notifyItemRangeRemoved(position, songList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedSongRVAdapter.ViewHolder {
        val binding: ItemLockerBinding = ItemLockerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedSongRVAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])

        holder.apply {
            // 재생 버튼 클릭 -> 정지 버튼으로 바꾸기
            binding.itemLockerPlayIv.setOnClickListener {
                // 재생 버튼 상태 설정
                val isPlaying = songStatusList[position]
                // 클릭한 아이템 상태 변경
                songStatusList[position] = !isPlaying
                if (isPlaying) {
                    binding.itemLockerPlayIv.setImageResource(R.drawable.btn_miniplayer_play)
                } else {
                    binding.itemLockerPlayIv.setImageResource(R.drawable.btn_miniplay_pause)
                }
//                mItemClickListener.onChangePlayState(position)
            }

            // 더보기 버튼 클릭 -> 삭제 진행
            binding.itemLockerMoreIv.setOnClickListener {
                mItemClickListener.onRemoveSong(songs[position].id)
                removeItem(position)
            }
        }
    }

    override fun getItemCount(): Int = songs.size

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