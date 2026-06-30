package com.example.data.mapper


import com.example.database.entity.MessageEntity
import com.example.database.entity.UserEntity
import com.example.model.message.MessageDto
import com.example.model.message.MessageStatus
import com.example.model.message.UserUi
import javax.inject.Inject

class ChatMapper @Inject constructor() {

    fun dtoToEntity(messageDto: MessageDto): MessageEntity {
        return MessageEntity(
            localId     = 0,                    // Room auto-generates
            serverId    = messageDto.id,         // backend msg_id → serverId
            clientId    = null,  // may be null for others' messages
            chatId      = messageDto.chat_id,
            senderId    = messageDto.sender_id,
            type        = messageDto.type,
            body        = messageDto.body,
            mediaId     = messageDto.media_id,
            mediaUrl    = messageDto.media_url,
            mediaThumb  = messageDto.media_thumb,
            replyToId   = messageDto.reply_to_id,
            createdAt   = messageDto.created_at,
            isForwarded = messageDto.is_forwarded,
            readByMe    = messageDto.read_by_me,
            status      = MessageStatus.SENT,
        )
    }

    fun dtoListToEntityList(messageList: List<MessageDto>): List<MessageEntity> {
        return messageList.map { dtoToEntity(it) }
    }

    fun createLocalMessage(
        chatId: Int,
        senderId: Int,
        type: String,
        body: String?,
        mediaId: Int?,
        replyToId: Int?,
        clientId: String,
    ): MessageEntity {
        return MessageEntity(
            localId     = 0,        // Room auto-generates on insert
            serverId    = null,     // unknown until API responds
            clientId    = clientId, // UUID supplied by the caller
            chatId      = chatId,
            senderId    = senderId,
            type        = type,
            body        = body,
            mediaId     = mediaId,
            mediaUrl    = null,
            mediaThumb  = null,
            replyToId   = replyToId,
            createdAt   = System.currentTimeMillis(),
            isForwarded = false,
            readByMe    = true,
            status      = MessageStatus.SENDING,
        )
    }


    fun toUserUi(entity: UserEntity): UserUi {
        return UserUi(
            id = entity.id,
            name = entity.name,
            profileUrl = entity.profileUrl ?: "",
            chatId = entity.chatId,
            isOnline = entity.isOnline
        )
    }
}
