package com.example.data.wrapper

import com.example.domain.data.NetworkResult
import com.example.model.ApiResponse
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
            NetworkResult.Error(e.message ?: "Unknown error")
        }
    }
}