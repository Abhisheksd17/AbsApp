package com.example.database.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.entity.AiMessageEntity
import com.example.database.entity.AiAttachmentsEntity
import com.example.database.entity.AiMessageWithAttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiDao
{

    @Upsert
    suspend fun insertMessageResponse(message: AiMessageEntity)

    @Upsert
    suspend fun insertAttachmentsResponse(attachments: List<AiAttachmentsEntity>)

    @Transaction
    @Query("SELECT * FROM ai_messages WHERE messageId = :messageId")
    suspend fun getMessageWithAttachments(messageId: String): AiMessageWithAttachmentEntity

    @Transaction
    @Query("SELECT * FROM ai_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
     fun getConversation(conversationId: String): Flow<List<AiMessageWithAttachmentEntity>>

    @Query("DELETE FROM ai_messages WHERE messageId = :messageId")
    suspend fun deleteMessage(messageId: String)


}