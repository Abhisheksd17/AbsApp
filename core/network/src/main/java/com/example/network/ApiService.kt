package com.example.network

import com.example.model.ApiResponse
import com.example.model.Response
import com.example.model.chat.ChatListResponse
import com.example.model.contact.ContactList
import com.example.model.contact.ContactSyncRequest
import com.example.model.login.AuthResponse
import com.example.model.login.AvatarResponse
import com.example.model.login.OtpRequest
import com.example.model.login.UpdateProfileRequest
import com.example.model.login.UserResponse
import com.example.model.login.VerifyOtpRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {

    @POST("/users/send-otp")
    suspend fun sendOtp(
        @Body request: OtpRequest
    ): retrofit2.Response<ApiResponse<Unit>>

    @POST("/users/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): retrofit2.Response<ApiResponse<AuthResponse>>

    @GET("/users/me")
    suspend fun getMe(): retrofit2.Response<ApiResponse<UserResponse>>

    @PUT("/users/me")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): retrofit2.Response<ApiResponse<UserResponse>>

    @Multipart
    @POST("/users/upload-avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): ApiResponse<AvatarResponse>


    @GET("/chats")
    suspend fun getChatList(): retrofit2.Response<ApiResponse<ChatListResponse>>

    @POST("contacts/sync")
    suspend fun syncContacts(
        @Body hashes: ContactSyncRequest
    ):retrofit2.Response<ApiResponse<List<ContactList>>>
}