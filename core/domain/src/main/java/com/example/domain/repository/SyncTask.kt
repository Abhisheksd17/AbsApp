package com.example.domain.repository

interface SyncTask {
    suspend fun sync()
}