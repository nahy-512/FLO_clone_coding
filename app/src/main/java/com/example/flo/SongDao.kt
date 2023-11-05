package com.example.flo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    // 모든 song의 목록을 조회
    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song>

    // id를 기준으로 song 데이터를 조회
    @Query("SELECT * FROM SongTable WHERE id = :id")
    fun getSong(id: Int): Song

    // song의 좋아요 여부를 수정
    @Query("UPDATE SongTable SET isLike = :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean, id: Int)

    // 좋아요한 song의 목록을 조회
    @Query("SELECT * FROM SongTable WHERE isLike = :isLike")
    fun getLikedSongs(isLike: Boolean): List<Song>
}