package com.example.data.mapper

import com.example.database.entity.ChatListEntity
import com.example.domain.data.ChatList
import com.example.model.chat.ChatListResponse
import javax.inject.Inject

class ChatListMapper @Inject constructor() {

    fun dtoToEntityList(response: ChatListResponse): List<ChatListEntity> {
        return response.chat.map { dto ->
            ChatListEntity(
                chatId = dto.chat_id,
                title = dto.title,
                userId = dto.user_id.toIntOrNull(),
                profileUrl = dto.profile_url,
                type = dto.type,

                lastMsgPreview = dto.last_msg_preview,
                lastMsgAt = dto.last_msg_at?.toLongOrNull(),
                lastMsgSenderId = dto.last_msg_sender_id,

                unreadCount = dto.unread_count ?: 0,
                peerOnline = dto.peer_online
            )
        }
    }

    fun entityToDomain(entity: ChatListEntity): ChatList {
        return ChatList(
            chatId = entity.chatId,
            title = entity.title,
            userId = entity.userId,
            profileUrl = entity.profileUrl,
            type = entity.type,

            lastMsgPreview = entity.lastMsgPreview,
            lastMsgAt = entity.lastMsgAt,
            lastMsgSenderId = entity.lastMsgSenderId,

            unreadCount = entity.unreadCount,
            peerOnline = entity.peerOnline
        )
    }

    fun entityListToDomainList(entities: List<ChatListEntity>): List<ChatList> {
        return entities.map { entityToDomain(it) }
    }


}