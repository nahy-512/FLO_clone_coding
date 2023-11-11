package com.example.flo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM UserTable")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM UserTable WHERE email = :email AND passward = :passward")
    fun getUser(email: String, passward: String): User?
}