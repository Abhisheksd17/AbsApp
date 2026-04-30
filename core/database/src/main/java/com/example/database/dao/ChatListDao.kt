package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.database.entity.ChatListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatListDao {

    @Query("SELECT * FROM chats ORDER BY lastMsgAt DESC")
    fun observeChats(): Flow<List<ChatListEntity>>

    @Upsert
    suspend fun insertChatList(chats: List<ChatListEntity>)

    @Query("DELETE FROM chats")
    suspend fun clearChats()

    @Query("SELECT * FROM chats ORDER BY lastMsgAt DESC")
    suspend fun getChatsOnce(): List<ChatListEntity>

    @Upsert
    suspend fun upsertChat(chat: ChatListEntity)

    @Query("DELETE FROM chats WHERE chatId = :chatId")
    suspend fun deleteChat(chatId: Int)
}