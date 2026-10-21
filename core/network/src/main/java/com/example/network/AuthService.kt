package com.example.network

import com.example.model.ApiResponse
import com.example.model.login.AuthResponse
import com.example.model.login.AvatarResponse
import com.example.model.login.OtpRequest
import com.example.model.login.RefreshTokenRequest
import com.example.model.login.UpdateProfileRequest
import com.example.model.login.UserResponse
import com.example.model.login.VerifyOtpRequest
import com.example.model.notification.TokenRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface AuthService {

    @POST("/users/send-otp")
    suspend fun sendOtp(
        @Body request: OtpRequest
    ): retrofit2.Response<ApiResponse<Unit>>

    @POST("/users/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): retrofit2.Response<ApiResponse<AuthResponse>>

    @POST("/users/refresh-token")
    fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Call<ApiResponse<AuthResponse>>

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

    @POST("/users/me/fcm-token")
    suspend  fun registerFcmToken(@Body body: TokenRequest): retrofit2.Response<ApiResponse<Unit>>

    @DELETE("me/fcm-token")
    suspend fun deRegisterFcmToken(@Body body: TokenRequest): retrofit2.Response<ApiResponse<Unit>>


}