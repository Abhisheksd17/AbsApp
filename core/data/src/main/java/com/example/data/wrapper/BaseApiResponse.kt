package com.example.data.wrapper

import com.example.data.wrapper.NetworkResult
import retrofit2.Response

open class BaseApiResponse {

    suspend fun <T : Any> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>
    ): NetworkResult<T> {
        return try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    if (body.status == 0 && body.data != null) {
                        NetworkResult.Success(body.data)
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
            NetworkResult.Error(e.message ?: "Unknown error")
        }
    }
}