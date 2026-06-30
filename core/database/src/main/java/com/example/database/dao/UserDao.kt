package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUsers(users: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUser(userId: Int): UserEntity?

    @Query(" SELECT chatId from users where id = :userId")
    suspend fun getChatId(userId: Int): Int?

    @Query("SELECT * FROM users WHERE chatId = :chatId LIMIT 1")
    fun getUserForChat(chatId: Int): Flow<UserEntity?>

    @Query("UPDATE users SET isOnline = :isOnline WHERE id = :userId")
    suspend fun updateOnlineStatus(userId: Int, isOnline: Boolean)
}