package com.example.domain.repository

interface ContactRepository {
    suspend fun syncContacts()

}