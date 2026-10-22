package com.example.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ai_attachments",
    foreignKeys = [
        ForeignKey(
            entity = AiMessageEntity::class,
            parentColumns = ["messageId"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("messageId")]
)
data class AiAttachmentsEntity(
    @PrimaryKey
    val id: String,

    val messageId: String,
    val filename: String,
    val mimetype: String,
    val size: Long,
    val status: String
)