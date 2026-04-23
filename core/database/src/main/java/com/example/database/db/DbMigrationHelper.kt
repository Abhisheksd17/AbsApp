package com.example.database.db

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbMigrationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun ensureEncryptedDb(dbName: String) {
        val prefs = context.getSharedPreferences("db_prefs", Context.MODE_PRIVATE)
        val done = prefs.getBoolean("encrypted_db_done", false)

        if (!done) {
            val dbFile = context.getDatabasePath(dbName)
            if (dbFile.exists()) {
                context.deleteDatabase(dbName) // removes old unencrypted DB
            }
            prefs.edit().putBoolean("encrypted_db_done", true).apply()
        }
    }
}