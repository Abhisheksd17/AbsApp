package com.example.data.mapper

import com.example.database.entity.AiAttachmentsEntity
import com.example.database.entity.AiMessageEntity
import com.example.database.entity.AiMessageWithAttachmentEntity
import com.example.model.ai.AiAttachmentUiModel
import com.example.model.ai.AiMessageUiModel
import com.example.model.ai.ChatMediaRequestDto
import com.example.model.ai.ChatRequestDto
import com.example.model.ai.ChatResponse
import java.util.UUID
import javax.inject.Inject

class AiMapper @Inject constructor() {

    fun ChatRequestDto.toUserMessageEntity(userMessageId: String = UUID.randomUUID().toString()): AiMessageEntity {
        return AiMessageEntity(
            messageId = userMessageId,
            conversationId = conversationId ?: "",
            role = "user",
            message = message,
            status = "SENT",
            timestamp = System.currentTimeMillis()
        )
    }

    fun ChatMediaRequestDto.toUserMessageEntity(userMessageId: String = UUID.randomUUID().toString()): AiMessageEntity {
        return AiMessageEntity(
            messageId = userMessageId,
            conversationId = conversationId ?: "",
            role = "user",
            message = message ?: "",
            status = "SENT",
            timestamp = System.currentTimeMillis()
        )
    }

    fun ChatMediaRequestDto.toUserAttachmentEntities(userMessageId: String): List<AiAttachmentsEntity> {
        return files.map { file ->
            AiAttachmentsEntity(
                id = UUID.randomUUID().toString(),
                messageId = userMessageId,
                filename = file.name,
                mimetype = file.extension,
                size = file.length(),
                status = "SENT"
            )
        }
    }

    fun ChatResponse.toMessageEntity(): AiMessageEntity {
        return AiMessageEntity(
            messageId = messageId,
            conversationId = conversationId,
            role = role,
            message = message,
            status = status,
            timestamp = System.currentTimeMillis()
        )
    }

    fun ChatResponse.toAttachmentEntities(): List<AiAttachmentsEntity> {
        return attachments.map { attachment ->
            AiAttachmentsEntity(
                id = attachment.id,
                messageId = messageId,
                filename = attachment.fileName,
                mimetype = attachment.mimeType,
                size = attachment.size.toLong(),
                status = attachment.status
            )
        }
    }

    fun entityListToUiModelList(messageList: List<AiMessageWithAttachmentEntity>): List<AiMessageUiModel> {
        return messageList.map { it.toUiModel() }
    }

    fun AiMessageWithAttachmentEntity.toUiModel(): AiMessageUiModel {
        return AiMessageUiModel(
            messageId = message.messageId,
            conversationId = message.conversationId,
            role = message.role,
            message = message.message,
            status = message.status,
            timestamp = message.timestamp,
            attachments = attachments.map { attachment ->
                AiAttachmentUiModel(
                    id = attachment.id,
                    filename = attachment.filename,
                    mimetype = attachment.mimetype,
                    size = attachment.size,
                    status = attachment.status
                )
            }
        )
    }

}