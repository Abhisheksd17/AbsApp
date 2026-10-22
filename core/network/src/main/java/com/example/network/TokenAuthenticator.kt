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
    private val apiServiceProvider: Provider<AuthService>,
) : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null

        // Stop retrying if retry limit reached
        if (responseCount(response) >= 3) {
            Log.e(TAG, "Failed to authenticate after multiple retries for URL: ${response.request.url}")
            return null
        }

        // Do not attempt to refresh if the request that failed is the refresh endpoint itself
        val requestPath = response.request.url.encodedPath
        if (requestPath.contains("/users/refresh-token")) {
            Log.e(TAG, "Refresh token request itself returned 401. Clearing tokens.")
            runBlocking { dataStore.clearToken() }
            return null
        }

        synchronized(this) {
            val refreshToken = runBlocking { dataStore.getRefreshToken() }
            val currentToken = runBlocking { dataStore.getAccessToken() }

            if (refreshToken.isNullOrEmpty()) {
                Log.w(TAG, "401 received for ${response.request.url}, but refresh token is null or empty. Clearing token.")
                runBlocking { dataStore.clearToken() }
                return null
            }

            // If another thread already updated the access token, retry with that new token
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            if ((requestToken != currentToken) && !currentToken.isNullOrEmpty()) {
                Log.d(TAG, "Token already refreshed by another thread. Retrying request with new token.")
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            Log.d(TAG, "401 received for ${response.request.url}. Calling /users/refresh-token API...")

            try {
                val apiService = apiServiceProvider.get()
                val refreshResponse = apiService.refreshToken(RefreshTokenRequest(refreshToken)).execute()

                if (refreshResponse.isSuccessful) {
                    val newAuthData = refreshResponse.body()?.data
                    if (newAuthData != null) {
                        Log.d(TAG, "Refresh token API call successful. Saving new tokens.")
                        runBlocking {
                            dataStore.saveUserToken(newAuthData)
                        }
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ${newAuthData.access_token}")
                            .build()
                    } else {
                        Log.e(TAG, "Refresh token API succeeded but body data was null.")
                    }
                } else {
                    Log.e(TAG, "Refresh token API failed with HTTP code: ${refreshResponse.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during refresh token API call: ${e.localizedMessage}", e)
            }

            // Refresh failed or returned invalid response: clear stored tokens
            runBlocking { dataStore.clearToken() }
            return null
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
