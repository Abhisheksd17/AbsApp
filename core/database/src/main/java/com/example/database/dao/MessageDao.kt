package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entity.MessageEntity
import com.example.model.message.MessageStatus
import com.example.model.message.MessageWithUser
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("""
        SELECT m.*,
               u.name AS senderName,
               u.profileUrl AS senderProfile
        FROM messages m
        LEFT JOIN users u ON m.senderId = u.id
        WHERE m.chatId = :chatId
        ORDER BY m.createdAt DESC, m.id DESC
    """)
    fun getMessagesWithUser(chatId: Int): Flow<List<MessageWithUser>>

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearChat(chatId: Int)

    @Query("""
    UPDATE messages 
    SET status = :status 
    WHERE clientId = :clientId
""")
    suspend fun updateMessageStatus(
        clientId: String,
        status: MessageStatus
    )
}