package com.example.database.db

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbKeyProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alias = "db_key_alias"

    fun getPassphrase(): ByteArray {
        val key = getOrCreateKey()
        return net.sqlcipher.database.SQLiteDatabase.getBytes(key.toCharArray())
    }

    private fun getOrCreateKey(): String {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        var key = prefs.getString("db_key", null)

        if (key == null) {
            key = UUID.randomUUID().toString() // generated once
            prefs.edit().putString("db_key", key).apply()
        }
        return key
    }
}