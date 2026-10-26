package com.example.database.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.entity.AiAttachmentsEntity
import com.example.database.entity.AiMessageEntity
import com.example.database.entity.AiMessageWithAttachmentEntity
import com.example.model.ai.AiConversationSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface AiDao {

    @Upsert
    suspend fun insertMessageResponse(message: AiMessageEntity)

    @Upsert
    suspend fun insertAttachmentsResponse(attachments: List<AiAttachmentsEntity>)

    @Transaction
    @Query("SELECT * FROM ai_messages WHERE messageId = :messageId")
    suspend fun getMessageWithAttachments(messageId: String): AiMessageWithAttachmentEntity

    @Transaction
    @Query("SELECT * FROM ai_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getConversationMessage(conversationId: String): Flow<List<AiMessageWithAttachmentEntity>>

    @Query(
        """
        SELECT conversationId,
               COALESCE(
                   (SELECT message FROM ai_messages m2 WHERE m2.conversationId = m1.conversationId AND m2.role = 'user' ORDER BY timestamp ASC LIMIT 1),
                   (SELECT message FROM ai_messages m2 WHERE m2.conversationId = m1.conversationId ORDER BY timestamp ASC LIMIT 1)
               ) AS conversationName,
               MAX(timestamp) AS lastUpdated,
               MAX(isPinned) AS isPinned
        FROM ai_messages m1
        GROUP BY conversationId
        ORDER BY lastUpdated DESC
        """,
    )
    fun getAllConversations(): Flow<List<AiConversationSummary>>

    @Query("UPDATE ai_messages SET isPinned = :isPinned WHERE conversationId = :conversationId")
    suspend fun updatePinStatus(conversationId: String, isPinned: Boolean)

    @Query("DELETE FROM ai_messages WHERE messageId = :messageId")
    suspend fun deleteMessage(messageId: String)
}