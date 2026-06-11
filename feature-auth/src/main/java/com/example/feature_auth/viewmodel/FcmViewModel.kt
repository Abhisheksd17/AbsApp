package com.example.feature_auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FcmViewModel @Inject constructor(
    private val repository: TokenRepository
) : ViewModel(){

    suspend  fun registerFcmToken(token: String) {

           repository.register(token)

    }
}