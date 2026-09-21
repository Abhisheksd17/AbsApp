package com.example.websocket

import android.util.Log
import com.example.model.websocket.WsCallAccepted
import com.example.model.websocket.WsCallEnded
import com.example.model.websocket.WsCallSignal
import com.example.model.websocket.WsEvent
import com.example.model.websocket.WsIncomingCall
import com.example.model.websocket.WsNewChat
import com.example.model.websocket.WsNewMessage
import com.example.model.websocket.WsReceipt
import com.example.model.websocket.WsTyping
import com.example.model.websocket.WsUserStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.WebSocketListener
import kotlinx.serialization.json.decodeFromJsonElement

@Singleton
class WebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val tag = "WebSocketManager"

    private val _events = MutableSharedFlow<WsEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<WsEvent> = _events.asSharedFlow()

    private var webSocket: WebSocket? = null
    @Volatile private var isConnected = false
    @Volatile private var shouldReconnect = true
    private var reconnectDelayMs = 2_000L


    fun connect(token: String, baseWsUrl: String) {
        shouldReconnect = true
        openSocket(token, baseWsUrl)
    }

    fun disconnect() {
        shouldReconnect = false
        webSocket?.close(1000, "User logout")
        webSocket = null
        isConnected = false
    }

    fun sendPing() = sendRaw("""{"event":"ping"}""")

    fun sendTyping(chatId: Int) =
        sendRaw("""{"event":"typing","chat_id":$chatId}""")

    fun sendStopTyping(chatId: Int) =
        sendRaw("""{"event":"stop_typing","chat_id":$chatId}""")

    fun sendAck(msgId: Long, status: String) =
        sendRaw("""{"event":"ack","msg_id":$msgId,"status":"$status"}""")

    private fun openSocket(token: String, baseWsUrl: String) {
        val request = Request.Builder()
            .url("$baseWsUrl/ws?token=$token")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(ws: WebSocket, response: Response) {
                isConnected = true
                reconnectDelayMs = 2_000L
                Log.d(tag, "WS connected")
                scope.launch { _events.emit(WsEvent.Connected) }
            }

            override fun onMessage(ws: WebSocket, text: String) {
                handleRaw(text)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e(tag, "WS failure: ${t.message}")
                isConnected = false
                scheduleReconnect(token, baseWsUrl)
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                isConnected = false
                scope.launch { _events.emit(WsEvent.Disconnected) }
                if (shouldReconnect) scheduleReconnect(token, baseWsUrl)
            }
        })
    }

    private fun handleRaw(text: String) {
        try {
            val element = json.parseToJsonElement(text)
            val obj = element.jsonObject
            val event = obj["event"]?.jsonPrimitive?.content

            when (event) {
                "connected" -> scope.launch {
                    _events.emit(WsEvent.Connected)
                }

                "new_message" -> {
                    val payload = json.decodeFromJsonElement<WsNewMessage>(obj)
                    scope.launch { _events.emit(WsEvent.NewMessage(payload)) }
                }

                "receipt" -> {
                    val payload = json.decodeFromJsonElement<WsReceipt>(obj)
                    scope.launch { _events.emit(WsEvent.Receipt(payload)) }
                }

                "typing" -> {
                    val payload = json.decodeFromJsonElement<WsTyping>(obj)
                    scope.launch { _events.emit(WsEvent.Typing(payload, true)) }
                }

                "stop_typing" -> {
                    val payload = json.decodeFromJsonElement<WsTyping>(obj)
                    scope.launch { _events.emit(WsEvent.Typing(payload, false)) }
                }
                "user_status" -> {
                    val payload = json.decodeFromJsonElement<WsUserStatus>(obj)
                    scope.launch { _events.emit(WsEvent.UserStatus(payload.user_id, payload.online)) }
                }
                "incoming_call" -> {
                    val payload = json.decodeFromJsonElement<WsIncomingCall>(obj)
                    scope.launch {
                        _events.emit(
                            WsEvent.IncomingCall(payload)
                        )
                    }
                }
                "call_accepted" -> {
                    val payload = json.decodeFromJsonElement<WsCallAccepted>(obj)
                    scope.launch {
                        _events.emit(
                            WsEvent.CallAccepted(payload)
                        )
                    }
                }
                "call_ended" -> {
                    val payload = json.decodeFromJsonElement<WsCallEnded>(obj)
                    scope.launch {
                        _events.emit(
                            WsEvent.CallEnded(payload)
                        )
                    }
                }

                "call_signal" -> {
                    val payload = json.decodeFromJsonElement<WsCallSignal>(obj)
                    scope.launch {
                        _events.emit(
                            WsEvent.CallSignal(payload)
                        )
                    }
                }
                "chat_created" -> {
                    val data = obj["data"]
                    if (data != null) {
                        val payload = json.decodeFromJsonElement<WsNewChat>(data)
                        scope.launch {
                            _events.emit(WsEvent.NewChat(payload))
                        }
                    }
                }

                "pong" -> Log.v(tag, "pong received")
                else -> Log.w(tag, "Unknown WS event: $event")
            }

        } catch (e: Exception) {
            Log.e(tag, "Failed to parse WS frame: $text", e)
        }
    }

    private fun sendRaw(json: String): Boolean {
        if (!isConnected || webSocket == null) return false
        return webSocket!!.send(json)
    }

    private fun scheduleReconnect(token: String, baseWsUrl: String) {
        if (!shouldReconnect) return
        scope.launch {
            Log.d(tag, "Reconnecting in ${reconnectDelayMs}ms")
            delay(reconnectDelayMs)
            reconnectDelayMs = (reconnectDelayMs * 2).coerceAtMost(30_000L)
            openSocket(token, baseWsUrl)
        }
    }
}