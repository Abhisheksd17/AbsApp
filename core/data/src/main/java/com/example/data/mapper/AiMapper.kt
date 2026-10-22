package com.example.data.mapper

import com.example.database.entity.AiAttachmentsEntity
import com.example.database.entity.AiMessageEntity
import com.example.database.entity.AiMessageWithAttachmentEntity
import com.example.model.ai.AiAttachmentUiModel
import com.example.model.ai.AiMessageUiModel
import com.example.model.ai.ChatResponse

class AiMapper {

    fun ChatResponse.toMessageEntity(): AiMessageEntity {
        return AiMessageEntity(
            messageId = messageId,
            conversationId = conversationId,
            role = role,
            message = message,
            status = status
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

    fun messagedtoListToEntiryList(messageList:List<AiMessageWithAttachmentEntity>):List<AiMessageUiModel>{
        return messageList.map { it.toUiModel() }
    }

    fun AiMessageWithAttachmentEntity.toUiModel(): AiMessageUiModel {
        return AiMessageUiModel(
            messageId = message.messageId,
            conversationId = message.conversationId,
            role = message.role,
            message = message.message,
            status = message.status,
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