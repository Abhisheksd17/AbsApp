package com.example.data.repositoryImpl

import android.util.Log
import com.example.common.datastore.DataStore
import com.example.data.wrapper.BaseApiResponse
import com.example.data.wrapper.WebSocketManager
import com.example.domain.data.NetworkResult
import com.example.domain.repository.AuthRepository
import com.example.model.ApiResponse
import com.example.model.login.OtpRequest
import com.example.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import com.example.model.login.AuthResponse
import com.example.model.login.AvatarResponse
import com.example.model.login.UpdateProfileRequest
import com.example.model.login.UserResponse
import com.example.model.login.VerifyOtpRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: DataStore,
    private val wsManager: WebSocketManager,
) : AuthRepository, BaseApiResponse() {


    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun connectWebSocket(baseWsUrl: String) {
        repoScope.launch {
            val token = dataStore.getAccessToken() ?: return@launch
            wsManager.connect(token, baseWsUrl)

        }
    }

    override fun disconnectWebSocket() {
        wsManager.disconnect()
        repoScope.coroutineContext.cancelChildren()
    }

    override suspend fun sendOtp(
        request: OtpRequest
    ): Flow<NetworkResult<Unit>> {

        return flow {
            emit(NetworkResult.Loading())

            val result = safeApiCall {
                apiService.sendOtp(request)
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    override suspend fun verifyOtp(
        request: VerifyOtpRequest
    ): Flow<NetworkResult<AuthResponse>> {

        return flow {
            emit(NetworkResult.Loading())

            val result = safeApiCall {
                apiService.verifyOtp(request)
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun updateProfile(
        request: UpdateProfileRequest
    ):Flow<NetworkResult<UserResponse>>{

        return flow{
            emit(NetworkResult.Loading())

            val result = safeApiCall {
                apiService.updateProfile(request)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)

        }


    override suspend fun uploadAvatar(
        file: File
    ): ApiResponse<AvatarResponse> {
        val part = createMultipart(file)
        return apiService.uploadAvatar(part)
    }

    fun createMultipart(file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaType())
        return MultipartBody.Part.createFormData(
            name = "file",
            filename = file.name,
            body = requestFile
        )
    }
}