package com.example.flo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    // 모든 album의 목록을 조회
    @Query("SELECT * FROM AlbumTable")
    fun getAlbums(): List<Album>
}