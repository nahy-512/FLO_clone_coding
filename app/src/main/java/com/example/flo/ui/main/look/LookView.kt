package com.example.flo.ui.main.look

import com.example.flo.data.remote.FloChartResult

interface LookView {
    fun onGetSongsLoading()
    fun onGetSongsSuccess(code: Int, result: FloChartResult)
    fun onGetSongsFailure(code: Int, message: String)
}