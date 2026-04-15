package com.example.common.datastore

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import javax.inject.Inject

import kotlinx.coroutines.runBlocking

class TokenInterceptor @Inject constructor(
    private val dataStore: DataStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {

        val token: String? = runBlocking {
            dataStore.getAccessToken()
        }

        val request = chain.request().newBuilder().apply {
            if (token != null && token.isNotEmpty()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(request)
    }
}