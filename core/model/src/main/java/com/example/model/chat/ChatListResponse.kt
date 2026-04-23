package com.example.model.chat

data class ChatListResponse(
    val chat:List<ChatDetails>
)

data class ChatDetails(
    val chat_id: Int,
    val type:String,
    val title:String,
    val user_id:String,
    val profile_url:String?,
    val last_msg_preview:String?,
    val last_msg_at:String?,
    val last_msg_sender_id:Int?,
    val unread_count:Int?,
    val peer_online:Boolean,

)
