package com.example.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: Int,
    val phoneHash: String,
    val lastModified: Long,
    val isSynced: Boolean,
    val isRegistered: Boolean,
    val name: String,
    val status: String?,
    val profile_url:String?
)
