package com.example.common.datastore

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val dataStore: DataStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val path = request.url.encodedPath

        // Skip adding Authorization header for auth/otp/refresh endpoints
        if (path.contains("/users/refresh-token") ||
            path.contains("/users/send-otp") ||
            path.contains("/users/verify-otp")
        ) {
            return chain.proceed(request)
        }

        val token: String? = runBlocking {
            dataStore.getAccessToken()
        }

        val newRequest = request.newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(newRequest)
    }
}