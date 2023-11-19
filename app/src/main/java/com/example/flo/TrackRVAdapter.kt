package com.example.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemTrackBinding

class TrackRVAdapter: RecyclerView.Adapter<TrackRVAdapter.ViewHolder>() {

    private var songs = ArrayList<Track>()

    fun addSongs(songs: ArrayList<Track>) {
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackRVAdapter.ViewHolder {
        val binding: ItemTrackBinding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TrackRVAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])

        holder.binding.itemTrackNumTv.text = "${position + 1}"
    }

    override fun getItemCount(): Int = songs.size


    inner class ViewHolder(val binding: ItemTrackBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Track) {
            binding.itemTrackTitleTv.text = song.title
            binding.itemTrackSingerTv.text = song.singer
            // 타이틀 여부
            if (song.isTitle) binding.itemTrackTitleMarkTv.visibility = View.VISIBLE
            else binding.itemTrackTitleMarkTv.visibility = View.GONE
        }
    }
}