package com.example.flo.ui.main.locker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.data.entities.Album
import com.example.flo.databinding.ItemLockerAlbumBinding

class SavedAlbumRVAdapter : RecyclerView.Adapter<SavedAlbumRVAdapter.ViewHolder>() {
    interface MyItemClickListener {
        fun onRemoveSong(albumId: Int) // 삭제 진행
    }

    private var albums = ArrayList<Album>()
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun addAlbums(albums: ArrayList<Album>) {
        this.albums.clear()
        this.albums.addAll(albums)

        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        albums.removeAt(position)
        notifyDataSetChanged()
//        notifyItemRemoved(position)
//        notifyItemRangeRemoved(position, songList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemLockerAlbumBinding = ItemLockerAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albums[position])

        holder.apply {
            // 앨범 클릭 시 삭제
            itemView.setOnClickListener {
                mItemClickListener.onRemoveSong(albums[position].id)
                removeItem(position)
            }
        }
    }

    override fun getItemCount(): Int = albums.size

    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class ViewHolder(val binding: ItemLockerAlbumBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.itemLockerAlbumTitleTv.text = album.title
            binding.itemLockerAlbumSingerTv.text = album.singer
            album.coverImg?.let { binding.itemLockerAlbumCoverImgIv.setImageResource(it) }
        }
    }
}