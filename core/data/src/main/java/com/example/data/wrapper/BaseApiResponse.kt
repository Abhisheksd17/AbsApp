package com.example.data.wrapper

import com.example.domain.data.NetworkResult
import com.example.model.ApiResponse
import org.json.JSONObject
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
                        NetworkResult.Error(body.message.ifBlank { "Server error (${body.status})" })
                    }
                } else {
                    NetworkResult.Error("Empty response body")
                }

            } else {
                val errorMsg = parseErrorMessage(response)
                NetworkResult.Error(errorMsg)
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

    private fun <T : Any> parseErrorMessage(response: Response<ApiResponse<T>>): String {
        val errorBodyStr = try {
            response.errorBody()?.string()
        } catch (_: Exception) {
            null
        }

        if (!errorBodyStr.isNullOrBlank()) {
            try {
                val json = JSONObject(errorBodyStr)
                if (json.has("message")) {
                    val msg = json.optString("message")
                    if (msg.isNotBlank()) {
                        return msg
                    }
                }
            } catch (_: Exception) {
            }
        }

        val httpMsg = response.message()
        if (!httpMsg.isNullOrBlank()) {
            return httpMsg
        }

        return "Server Error (${response.code()})"
    }
}