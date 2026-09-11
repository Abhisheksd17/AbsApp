package com.example.network

import android.util.Log
import com.example.common.datastore.DataStore
import com.example.model.login.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val dataStore: DataStore,
    private val apiServiceProvider: Provider<ApiService>
) : Authenticator {


    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null


        synchronized(this) {
            val refreshToken = runBlocking { dataStore.getRefreshToken() }
            val currentToken = runBlocking { dataStore.getAccessToken() }

            if (refreshToken == null) {
                return null
            }

            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            if (requestToken != currentToken && currentToken != null) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val apiService = apiServiceProvider.get()
            val refreshResponse = apiService.refreshToken(RefreshTokenRequest(refreshToken)).execute()

            if (refreshResponse.isSuccessful) {
                val newAuthData = refreshResponse.body()?.data
                if (newAuthData != null) {
                    runBlocking {
                        dataStore.saveUserToken(newAuthData)
                    }
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer ${newAuthData.access_token}")
                        .build()
                }
            }

            runBlocking { dataStore.clearToken() }
            return null
        }
    }
}
