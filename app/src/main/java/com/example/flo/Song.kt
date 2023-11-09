package com.example.flo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SongTable")
data class Song(
    var title: String = "", // 제목
    var singer: String = "", // 가수
    var coverImg: Int? = null, // 사진
    var second: Int = 0, // 재생 시간
    var playTime: Int = 0, // 현재 재생 시간
    var isPlaying: Boolean = false, // 재생중인지
    var music: String = "", // 어떤 음악이 재생되고 있었는지 알려주는 변수
    var isLike: Boolean = false, // 좋아요 여부
    var isTitle: Boolean = false,
    var albumIdx: Int? = 0 // 소속 앨범
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
