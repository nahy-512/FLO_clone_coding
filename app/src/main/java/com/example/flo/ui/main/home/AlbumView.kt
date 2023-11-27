package com.example.flo.ui.main.home

import com.example.flo.data.remote.AlbumResult
import com.example.flo.data.remote.AlbumTracks


interface HomeView {
    fun onGetAlbumsLoading()
    fun onGetAlbumsSuccess(code: Int, result: AlbumResult)
    fun onGetAlbumsFailure(code: Int, message: String)
}

interface AlbumView {
    fun onGetAlbumTracksLoading()
    fun onGetAlbumTracksSuccess(code: Int, result: List<AlbumTracks>)
    fun onGetAlbumTracksFailure(code: Int, message: String)
}