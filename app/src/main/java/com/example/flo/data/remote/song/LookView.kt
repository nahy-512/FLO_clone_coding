package com.example.flo.data.remote.song

import com.example.flo.data.remote.song.FloChartResult

interface LookView {
    fun onGetSongsLoading()
    fun onGetSongsSuccess(code: Int, result: FloChartResult)
    fun onGetSongsFailure(code: Int, message: String)
}