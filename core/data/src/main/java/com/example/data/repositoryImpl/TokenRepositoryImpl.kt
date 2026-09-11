package com.example.data.repositoryImpl

import android.util.Log
import com.example.domain.repository.TokenRepository
import com.example.model.notification.TokenRequest
import com.example.network.ApiService
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): TokenRepository {


    override suspend  fun register(token: String, userId: Int) {
        try {
            val response =  apiService.registerFcmToken(TokenRequest(token, userId))
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

}