package com.example.model.websocket

sealed class WsEvent {
    data class NewMessage(val payload: WsNewMessage) : WsEvent()
    data class Receipt(val payload: WsReceipt) : WsEvent()
    data class Typing(val payload: WsTyping, val isTyping: Boolean) : WsEvent()
    object Connected : WsEvent()
    object Disconnected : WsEvent()

    data class UserStatus(val userId: Int, val isOnline: Boolean) : WsEvent()
    data class IncomingCall(val payload: WsIncomingCall) : WsEvent()
    data class CallAccepted(val payload: WsCallAccepted) : WsEvent()
    data class CallEnded(val payload: WsCallEnded) : WsEvent()

    data class CallSignal(val payload: WsCallSignal) : WsEvent()
}
