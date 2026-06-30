package com.example.feature_auth.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.datastore.DataStore
import com.example.domain.data.NetworkResult
import com.example.domain.repository.AuthRepository
import com.example.model.event.AuthEvent
import com.example.model.login.AuthResponse
import com.example.model.login.OtpRequest
import com.example.model.login.UpdateProfileRequest
import com.example.model.login.UserResponse
import com.example.model.login.VerifyOtpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

const val SOCKET_URL = "wss://absapp-backend.onrender.com"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val datastore: DataStore,
) : ViewModel() {

    private val _sendOtpState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle())
    val sendOtpState = _sendOtpState.asStateFlow()

    private val _verifyOtpState = MutableStateFlow<NetworkResult<AuthResponse>>(NetworkResult.Idle())
    val verifyOtpState = _verifyOtpState.asStateFlow()

    private val _profileState = MutableStateFlow<NetworkResult<UserResponse>>(NetworkResult.Idle())
    val profileState = _profileState.asStateFlow()

    private val _event = MutableSharedFlow<AuthEvent>()
    val event = _event.asSharedFlow()

    val userProfile = datastore.getUserProfile()

    suspend fun getToken():String?{
        return datastore.getAccessToken()
    }

    fun connectSocket(){
            authRepository.connectWebSocket(SOCKET_URL)

    }


    fun sendOtp(phone: String) {
        viewModelScope.launch {
            authRepository.sendOtp(OtpRequest(phone)).collect { response ->

                _sendOtpState.value = response

                if (response is NetworkResult.Success) {
                    datastore.saveNumber(phone)
                    _event.emit(AuthEvent.NavigateToOtp)
                }
            }
        }
    }


    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            val phone = datastore.getNumber().first() ?: ""
            if (phone.isEmpty()) {
                _verifyOtpState.value = NetworkResult.Error("Phone not found")
                return@launch
            }

            authRepository.verifyOtp(VerifyOtpRequest(phone, otp)).collect { response ->
                _verifyOtpState.value = response
                if (response is NetworkResult.Success) {

                    response.data?.let { user ->
                        datastore.saveUserData(user)
                    }
                    _event.emit(AuthEvent.NavigateToUpdateProfile)
                }
            }

        }
    }

    fun updateProfile(
        userName: String,
        status: String,
        file: File?
    ) {
        viewModelScope.launch {


            var avatarKey: String? = null

            if (file != null) {
                val uploadResponse = authRepository.uploadAvatar(file)
                if (uploadResponse.status == 0) {
                    avatarKey = uploadResponse.data?.avatar_key
                } else {
                    _profileState.value = NetworkResult.Error("Image upload failed")
                    return@launch
                }
            }

            authRepository.updateProfile(
                UpdateProfileRequest(userName, status, avatarKey)
            ).collect { response ->

                _profileState.value = response
                if(response is NetworkResult.Success){

                    _event.emit(AuthEvent.NavigateToHome)
                }

                }
            }

        }
    }


