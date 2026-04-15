package com.example.domain.repository

import android.net.Uri
import com.example.domain.data.NetworkResult
import com.example.model.ApiResponse
import com.example.model.Response
import com.example.model.login.AuthResponse
import com.example.model.login.AvatarResponse
import com.example.model.login.OtpRequest
import com.example.model.login.UpdateProfileRequest
import com.example.model.login.UserResponse
import com.example.model.login.VerifyOtpRequest
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AuthRepository {

    suspend fun sendOtp(request: OtpRequest): Flow<NetworkResult<Unit>>
    suspend fun verifyOtp(request: VerifyOtpRequest): Flow<NetworkResult<AuthResponse>>
    suspend fun updateProfile(request: UpdateProfileRequest): Flow<NetworkResult<UserResponse>>
    suspend fun uploadAvatar(file: File): ApiResponse<AvatarResponse>

}