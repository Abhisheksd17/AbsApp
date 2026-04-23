package com.example.database.db

import android.content.Context
import androidx.room.Room
import com.example.database.dao.ChatDao
import com.example.database.dao.ContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "absapp_chat_database"

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        keyProvider: DbKeyProvider,
        migrationHelper: DbMigrationHelper
    ): ChatRoomDataBase {

        migrationHelper.ensureEncryptedDb(DB_NAME)

        val passphrase = keyProvider.getPassphrase()
        val factory = net.sqlcipher.database.SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            ChatRoomDataBase::class.java,
            DB_NAME
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideChatDao(db: ChatRoomDataBase): ChatDao = db.chatDao()

    @Provides
    fun provideContactDao(db: ChatRoomDataBase): ContactDao = db.contactDao()
}