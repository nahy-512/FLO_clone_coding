package com.example.flo


interface HomeView {
    fun onGetAlbumsLoading()
    fun onGetAlbumsSuccess(code: Int, result: AlbumResult)
    fun onGetAlbumsFailure(code: Int, message: String)
}