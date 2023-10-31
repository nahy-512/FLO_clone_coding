package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemLockerBinding

class LockerRVAdapter(private val songList: ArrayList<Song>): RecyclerView.Adapter<LockerRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onRemoveSong(position: Int) // 삭제 진행
    }

    private lateinit var mItemClickListener: LockerRVAdapter.MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun removeItem(position: Int) {
        songList.removeAt(position)
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
        holder.binding.itemLockerMoreIv.setOnClickListener { mItemClickListener.onRemoveSong(position) }
    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(val binding: ItemLockerBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.itemAlbumTitleTv.text = song.title
            binding.itemLockerSingerTv.text = song.singer
            song.coverImg?.let { binding.itemLockerCoverImgIv.setImageResource(it) }
        }
    }

}