package com.example.flo

import androidx.lifecycle.LiveData
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
    fun getAllAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable")
    fun getAlbumsLiveData(): LiveData<List<Album>>

    /* 좋아요 관련 */
    @Insert
    fun likeAlbum(like: Like)

    // 사용자가 좋아요한 LikeTable의 아이디 반환
    @Query("SELECT id FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun isLikedAlbum(userId: Int, albumId: Int): Int?

    // 좋아요 취소
    @Query("DELETE FROM LikeTable WHERE userId = :userId AND albumID = :albumId")
    fun dislikedAlbum(userId: Int, albumId: Int)

    // 사용자가 좋아요한 앨범 목록 가져오기
    @Query("SELECT AT.* FROM LikeTable as LT LEFT JOIN AlbumTable as AT ON LT.albumId = AT.id WHERE LT.userId = :userId")
    fun getUserLikedAlbums(userId: Int): List<Album>
}