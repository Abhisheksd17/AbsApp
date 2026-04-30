package com.example.domain.repository

import com.example.domain.data.NetworkResult
import com.example.model.contact.ContactDomain
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    suspend fun syncContacts()
    fun fetchContacts(): Flow<NetworkResult<List<ContactDomain>>>
    fun syncRefreshContact(): Flow<NetworkResult<List<ContactDomain>>>


}