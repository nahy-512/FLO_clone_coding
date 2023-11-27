package com.example.flo.data.remote

import com.google.gson.annotations.SerializedName


/** 앨범 리스트 조회 */
data class AlbumResponse (
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: AlbumResult
)

data class AlbumResult(
    val albums: ArrayList<Albums>
)

data class Albums (
    @SerializedName("albumIdx") val id: Int,
    val singer: String,
    val title: String,
    val coverImgUrl: String
)

/** 앨범 수록곡 조회 */
data class AlbumTrackResponse (
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<AlbumTracks>
)

data class AlbumTracks (
    @SerializedName("songIdx") val id: Int,
    val title: String,
    val singer: String,
    val isTitleSong: String
)