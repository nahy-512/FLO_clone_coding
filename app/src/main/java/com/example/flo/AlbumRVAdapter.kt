package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemAlbumBinding

class AlbumRVAdapter(private val albumList: ArrayList<Album>): RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onItemClick(album: Album)
//        fun onPlayBtnClick(song: Song)
        fun onPlayBtnClick(album: Album)
    }

    private lateinit var mItemClickListener: MyItemClickListener // 전달받은 리스너 객체를 저장할 변수
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) { // 외부에서 전달받을 수 있는 함수
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlbumRVAdapter.ViewHolder {
        val binding: ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])
        // 전체 아이템 클릭 -> 화면 전환
        holder.itemView.setOnClickListener { mItemClickListener.onItemClick(albumList[position]) }
        // 재생 버튼 클릭 -> 데이터 전달
//        holder.binding.itemAlbumPlayIv.setOnClickListener { mItemClickListener.onPlayBtnClick(
//            albumList[position].song?.get(0) ?: Song()
//        ) }
        holder.binding.itemAlbumPlayIv.setOnClickListener { mItemClickListener.onPlayBtnClick(albumList[position]) }
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
            binding.itemAlbumCoverImgIv.setImageResource(album.coverImg!!)
        }
    }
}