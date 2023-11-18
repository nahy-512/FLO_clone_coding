package com.example.flo

interface LookView {
    fun onGetSongsLoading()
    fun onGetSongsSuccess(code: Int, result: FloChartResult)
    fun onGetSongsFailure(code: Int, message: String)
}