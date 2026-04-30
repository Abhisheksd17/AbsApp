package com.example.model.contact

data class ContactList(
    val user_id:Int,
    val phone_hash: String,
    val name: String,
    val status_text: String?,
    val profile_url:String?
)


