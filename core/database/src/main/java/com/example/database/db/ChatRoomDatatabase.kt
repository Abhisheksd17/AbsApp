package com.example.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.ChatDao
import com.example.database.dao.ContactDao
import com.example.database.entity.ChatListEntity
import com.example.database.entity.ContactEntity

@Database(
    entities = [
        ChatListEntity::class,
        ContactEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ChatRoomDataBase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun contactDao(): ContactDao
}