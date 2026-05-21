package com.example.domain.data

import com.example.model.call.CallParams
import com.example.model.call.IncomingCallEvent

sealed class CallState {
    object Idle                                  : CallState()
    object Ended                                  : CallState()
    object Loading                               : CallState()
    data class Ringing(val params: CallParams)   : CallState()
    data class Incoming(val event: IncomingCallEvent) : CallState()
    data class Active(val params: CallParams)    : CallState()
    data class Error(val message: String)        : CallState()
}
