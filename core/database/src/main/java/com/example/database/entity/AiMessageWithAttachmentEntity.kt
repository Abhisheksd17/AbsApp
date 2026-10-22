package com.example.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AiMessageWithAttachmentEntity(
    @Embedded
    val message: AiMessageEntity,

    @Relation(
        parentColumn = "messageId",
        entityColumn = "messageId"
    )
    val attachments: List<AiAttachmentsEntity>
)