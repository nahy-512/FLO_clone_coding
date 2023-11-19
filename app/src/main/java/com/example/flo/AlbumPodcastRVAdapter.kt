package com.example.flo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flo.databinding.ItemAlbumBinding

class AlbumPodcastRVAdapter(val context: Context, val result: AlbumResult): RecyclerView.Adapter<AlbumPodcastRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(album: Albums)
//        fun onPlayBtnClick(albumIdx: Int)
    }

    private lateinit var mItemClickListener: AlbumPodcastRVAdapter.MyItemClickListener // 전달받은 리스너 객체를 저장할 변수
    fun setMyItemClickListener(itemClickListener: AlbumPodcastRVAdapter.MyItemClickListener) { // 외부에서 전달받을 수 있는 함수
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumPodcastRVAdapter.ViewHolder {
        val binding: ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlbumPodcastRVAdapter.ViewHolder, position: Int) {
        val item = result.albums[position]

        if (item.coverImgUrl.isBlank()) {
            Log.d("image", "isEmpty")
        } else {
            Log.d("image", item.coverImgUrl)
            Glide.with(context).load(item.coverImgUrl).into(holder.coverImg)
        }
        holder.title.text = item.title
        holder.singer.text = item.singer

        holder.itemView.setOnClickListener { mItemClickListener.onItemClick(item) }
    }

    override fun getItemCount(): Int = result.albums.size

    inner class ViewHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root) {

        val coverImg: ImageView = binding.itemAlbumCoverImgIv
        val title: TextView = binding.itemLockerSongTitleTv
        val singer: TextView = binding.itemAlbumSingerTv

    }
}