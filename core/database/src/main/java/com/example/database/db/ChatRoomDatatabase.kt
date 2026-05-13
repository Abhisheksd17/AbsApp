package com.example.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.MessageDao
import com.example.database.dao.ChatListDao
import com.example.database.dao.ContactDao
import com.example.database.dao.UserDao
import com.example.database.entity.ChatListEntity
import com.example.database.entity.ContactEntity
import com.example.database.entity.UserEntity
import com.example.database.entity.MessageEntity
import com.example.database.entity.ChatEntity

@Database(
    entities = [
        ChatListEntity::class,
        ContactEntity::class,
        UserEntity::class,
        MessageEntity::class,
    ],
    version = 11,
    exportSchema = false
)
abstract class ChatRoomDataBase : RoomDatabase() {
    abstract fun chatListDao(): ChatListDao
    abstract fun contactDao(): ContactDao
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao

}
