package com.example.flo

import com.google.gson.annotations.SerializedName


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