package com.example.model.login

data class VerifyOtpRequest(
    val phone: String?,
    val otp: String
)
