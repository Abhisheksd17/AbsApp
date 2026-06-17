package com.example.feature_auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.datastore.DataStore
import com.example.domain.repository.TokenRepository
import com.example.domain.usecase.FcmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FcmViewModel @Inject constructor(
    private val fcmManager: FcmUseCase,
    private val dataStore: DataStore
) : ViewModel(){

    fun registerFcmToken(token: String) {
        viewModelScope.launch {
            val userId = dataStore.getUserId() ?: return@launch

            dataStore.saveFcmToken(token)

            fcmManager.registerToken(token, userId)
        }
    }


}