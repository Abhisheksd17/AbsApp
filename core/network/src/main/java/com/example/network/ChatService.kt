package com.example.network

import com.example.model.ApiResponse
import com.example.model.call.AcceptCallRequest
import com.example.model.call.CallTokenResponse
import com.example.model.call.EndCallRequest
import com.example.model.call.InitiateCallRequest
import com.example.model.chat.CreateChatRequest
import com.example.model.chat.CreateChatResponse
import com.example.model.chat.CreateGroupRequest
import com.example.model.chatlist.ChatDetails
import com.example.model.message.MessagesResponse
import com.example.model.message.SendMessageRequest
import com.example.model.contact.ContactList
import com.example.model.contact.ContactSyncRequest
import com.example.model.media.MediaUploadRequest
import com.example.model.media.MediaUploadResponse
import com.example.model.message.SendMessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatService {

    @GET("/chats")
    suspend fun getChatList(): retrofit2.Response<ApiResponse<List<ChatDetails>>>

    @POST("/users/contacts/sync")
    suspend fun syncContacts(
        @Body hashes: ContactSyncRequest
    ):retrofit2.Response<ApiResponse<List<ContactList>>>

    @GET("messages/{chatId}/messages")
    suspend fun getMessages(
        @Path("chatId") chatId: Int,
        @Query("limit") limit: Int,
        @Query("cursor") cursor: String? = null
    ): retrofit2.Response<ApiResponse<MessagesResponse>>

    @POST("messages")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): retrofit2.Response<ApiResponse<SendMessageResponse>>

    @POST("chats/direct")
    suspend fun createDirectChat(
        @Body request: CreateChatRequest
    ): retrofit2.Response<ApiResponse<CreateChatResponse>>

    @POST("chats/group")
    suspend fun createGroupChat(
        @Body request: CreateGroupRequest
    ): retrofit2.Response<ApiResponse<CreateChatResponse>>

    @POST("/media")
    suspend fun uploadMedia(
        @Body request: MediaUploadRequest
        ): retrofit2.Response<ApiResponse<MediaUploadResponse>>

    @POST("call/call/initiate")
    suspend fun initiateCall(@Body body: InitiateCallRequest): retrofit2.Response<ApiResponse<CallTokenResponse>>

    @POST("call/call/accept")
    suspend fun acceptCall(@Body body: AcceptCallRequest): retrofit2.Response<ApiResponse<CallTokenResponse>>

    @POST("call/call/end")
    suspend fun endCall(@Body body: EndCallRequest):retrofit2.Response<ApiResponse<Unit>>


}