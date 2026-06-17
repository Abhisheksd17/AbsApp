package com.example.domain.repository

interface TokenRepository {

    suspend  fun register(token: String, userId: Int)
}
