package com.example.data.wrapper

import com.example.domain.data.NetworkResult
import com.example.model.ApiResponse
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

open class BaseApiResponse {

    suspend fun <T : Any> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>
    ): NetworkResult<T> {
        return try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    if (body.status == 0) {

                        body.data?.let {
                            NetworkResult.Success(it)
                        } ?: run {
                            @Suppress("UNCHECKED_CAST")
                            NetworkResult.Success(Unit as T)
                        }

                    } else {
                        NetworkResult.Error(body.message)
                    }
                } else {
                    NetworkResult.Error("Empty response body")
                }

            } else {
                NetworkResult.Error(response.message())
            }

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            val errorMessage= when(e){
                is java.net.UnknownHostException -> "No internet connection"
                is java.net.SocketTimeoutException -> "Connection timeout"
                is java.net.ConnectException -> "Connection error"
                is java.net.NoRouteToHostException -> "No route to host"
                is java.net.ProtocolException -> "Protocol error"
                is java.net.UnknownServiceException -> "Unknown service error"
                is java.net.SocketException -> "Socket error"
                else -> e.message ?: "Unknown error"
            }
            NetworkResult.Error(errorMessage ?: "Unknown error")
        }
    }
}