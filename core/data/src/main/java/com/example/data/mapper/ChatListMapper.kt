package com.example.data.mapper

import com.example.database.entity.ChatListEntity
import com.example.domain.data.ChatList
import com.example.model.chatlist.ChatDetails
import javax.inject.Inject

class ChatListMapper @Inject constructor() {

    fun dtoToEntity(dto: ChatDetails): ChatListEntity {

            return ChatListEntity(
                chatId = dto.chat_id,
                title = dto.title,
                userId = dto.user_id,
                profileUrl = dto.profile_url,
                type = dto.type,

                lastMsgPreview = dto.last_msg_preview,
                lastMsgAt = dto.last_msg_at,
                lastMsgSenderId = dto.last_msg_sender_id,

                unreadCount = dto.unread_count ?: 0,
                peerOnline = dto.peer_online
            )

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

    fun dtoToEntityList(entities: List<ChatDetails>): List<ChatListEntity> {
        return entities.map { dtoToEntity(it) }
    }

    fun wsDtoToEntity(dto: com.example.model.websocket.WsNewChat): ChatListEntity {
        return ChatListEntity(
            chatId = dto.chat_id,
            title = dto.title,
            userId = dto.user_id,
            profileUrl = dto.profile_url,
            type = dto.type,
            lastMsgPreview = dto.last_msg_preview,
            lastMsgAt = dto.last_msg_at,
            lastMsgSenderId = dto.last_msg_sender_id,
            unreadCount = dto.unread_count ?: 0,
            peerOnline = dto.peer_online
        )
    }


}