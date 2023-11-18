package com.example.flo

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface SongRetrofitInterface {
    @GET("/songs")
    fun getSongs(): Call<SongResponse>
}