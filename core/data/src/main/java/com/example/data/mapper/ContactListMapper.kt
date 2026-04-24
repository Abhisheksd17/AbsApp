package com.example.data.mapper

import com.example.database.entity.ContactEntity
import com.example.model.contact.ContactDomain
import javax.inject.Inject

class ContactListMapper @Inject constructor()  {
    fun entityToDomain(entity: ContactEntity): ContactDomain{
        return ContactDomain(
            id = entity.id,
            name = entity.name,
            status = entity.status,
            profile_url = entity.profile_url
        )
    }


    fun entityListToDomainList(entities: List<ContactEntity>): List<ContactDomain> {
        return entities.map { entityToDomain(it) }
    }
    }
