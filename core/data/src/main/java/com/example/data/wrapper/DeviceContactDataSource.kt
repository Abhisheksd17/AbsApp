package com.example.data.wrapper

import android.content.Context
import android.provider.ContactsContract
import com.example.common.util.Utils
import com.example.model.contact.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DeviceContactDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getContacts(): List<Contact> {
        val list = mutableListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
        )

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(0)
                val phone = it.getString(1)
                val lastModified = it.getLong(2)

                list.add(
                    Contact(
                        id = id,
                        phoneHash = Utils.sha256(normalize(phone)),
                        lastModified = lastModified
                    )
                )
            }
        }

        return list
    }

    private fun normalize(phone: String): String {
        return phone.replace("\\s".toRegex(), "")
    }
}