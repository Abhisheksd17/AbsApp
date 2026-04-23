package com.example.data.repositoryImpl

import com.example.common.util.DeviceContactDataSource
import com.example.database.dao.ContactDao
import com.example.database.entity.ContactEntity
import com.example.domain.repository.ContactRepository
import com.example.model.contact.ContactSyncRequest
import com.example.network.ApiService
import javax.inject.Inject
import kotlin.collections.emptyList

class ContactRepositoryImpl @Inject constructor(
    private val dao: ContactDao,
    private val api: ApiService,
    private val deviceSource: DeviceContactDataSource,
) : ContactRepository {


    override suspend fun syncContacts() {

        val deviceContacts = deviceSource.getContacts()
        if (deviceContacts.isEmpty()) return

        val batches = deviceContacts.chunked(100)

        val finalList = mutableListOf<ContactEntity>()

        for (batch in batches) {
            val response = api.syncContacts(
                ContactSyncRequest(
                    hashes = batch.map { it.phoneHash }
                )
            )
            if (response.isSuccessful) {
                val registered = response.body()?.data ?: emptyList()
                val mapped = registered.map {
                    ContactEntity(
                        id = it.phone_hash,
                        phoneHash = it.phone_hash,
                        lastModified = System.currentTimeMillis(),
                        isSynced = true,
                        isRegistered = true,
                        name = it.name,
                        status = it.status_text
                    )
                }
                finalList.addAll(mapped)

            } else {
                throw Exception("Sync failed")
            }
        }

        dao.replaceAll(finalList)
    }

}