package com.example.model.event

sealed class AuthEvent {
    object NavigateToHome : AuthEvent()
    object NavigateToUpdateProfile : AuthEvent()
    object NavigateToOtp : AuthEvent()
}