package com.example.flo

data class Song(
    val title: String = "",
    val singer: String = "",
    var second: Int = 0,
    var playTime: Int = 0,
    var isPlaying: Boolean = false,
    var music: String = "" // 어떤 음악이 재생되고 있었는지 알려주는 변수
)
