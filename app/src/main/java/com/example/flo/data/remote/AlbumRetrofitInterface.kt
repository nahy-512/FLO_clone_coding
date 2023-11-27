package com.example.flo.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumRetrofitInterface {
    /** 앨범 리스트 조회 */
    @GET("/albums")
    fun getAlbums(): Call<AlbumResponse>

    /** 앨범 수록곡 조회 */
    @GET("/albums/{albumIdx}")
    fun getAlbumTracks(
        @Path("albumIdx") albumIdx: Int
    ): Call<AlbumTrackResponse>
}