package com.example.data.repositoryImpl

import com.example.data.mapper.ContactListMapper
import com.example.data.wrapper.DeviceContactDataSource
import com.example.database.dao.ContactDao
import com.example.database.entity.ContactEntity
import com.example.domain.data.NetworkResult
import com.example.domain.repository.ContactRepository
import com.example.model.contact.ContactSyncRequest
import com.example.model.contact.ContactDomain
import com.example.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.emptyList


class ContactRepositoryImpl @Inject constructor(
    private val dao: ContactDao,
    private val api: ApiService,
    private val mapper: ContactListMapper,
    private val deviceSource: DeviceContactDataSource
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
                        id = it.user_id,
                        phoneHash = it.phone_hash,
                        lastModified = System.currentTimeMillis(),
                        isSynced = true,
                        isRegistered = true,
                        name = it.name,
                        status = it.status_text,
                        profile_url = it.profile_url
                    )
                }
                finalList.addAll(mapped)

            } else {
                throw Exception("Sync failed")
            }
        }

        dao.replaceAll(finalList)
    }

    override fun fetchContacts(): Flow<NetworkResult<List<ContactDomain>>> {
        return dao.getAllContacts()
            .map<List<ContactEntity>, NetworkResult<List<ContactDomain>>> { entities ->
                val domainList = mapper.entityListToDomainList(entities)
                NetworkResult.Success(domainList)
            }
            .catch { e ->
                emit(NetworkResult.Error(e.message ?: "DB error"))
            }
    }


    override fun syncRefreshContact(): Flow<NetworkResult<List<ContactDomain>>> = flow {

        emit(NetworkResult.Loading())

        try {
            syncContacts()
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Sync failed"))
        }

        emitAll(
            dao.getAllContacts().map { entities ->
                val domainList = mapper.entityListToDomainList(entities)
                NetworkResult.Success(domainList)
            }
        )

    }.catch { e ->
        emit(NetworkResult.Error(e.message ?: "Unknown error"))
    }


}