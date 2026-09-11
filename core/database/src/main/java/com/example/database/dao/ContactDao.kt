package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("DELETE FROM contacts")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<ContactEntity>)


    @Transaction
    suspend fun replaceAll(list: List<ContactEntity>) {
        clearAll()
        insertAll(list)
    }

    @Query("SELECT * FROM contacts order by name asc")
     fun getAllContacts(): Flow<List<ContactEntity>>
}