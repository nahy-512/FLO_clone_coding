package com.example.flo

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flo.databinding.ItemChartBinding

class SongRVAdapter(val context: Context, val result: FloChartResult): RecyclerView.Adapter<SongRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongRVAdapter.ViewHolder {
        val binding: ItemChartBinding = ItemChartBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SongRVAdapter.ViewHolder, position: Int) {
//        holder.bind(result.songs[position])
        val item = result.songs[position]

        if (item.coverImgUrl.isBlank()) {
            Log.d("image", "isEmpty")
        } else {
            Log.d("image", item.coverImgUrl)
            Glide.with(context).load(item.coverImgUrl).into(holder.coverImg)
        }
        holder.ranking.text = (position + 1).toString()
        holder.title.text = item.title
        holder.singer.text = item.singer
    }

    override fun getItemCount(): Int = result.songs.size

    inner class ViewHolder(val binding: ItemChartBinding): RecyclerView.ViewHolder(binding.root) {

        val ranking: TextView = binding.itemChartRankingTv
        val coverImg: ImageView = binding.itemChartCoverImgIv
        val title: TextView = binding.itemChartTitleTv
        val singer: TextView = binding.itemChartSingerTv

    }
}