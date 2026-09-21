package com.example.data.repositoryImpl

import com.example.data.wrapper.BaseApiResponse
import com.example.websocket.WebSocketManager
import com.example.domain.data.NetworkResult
import com.example.domain.repository.CallRepository
import com.example.model.call.AcceptCallRequest
import com.example.model.call.CallTokenResponse
import com.example.model.call.EndCallRequest
import com.example.model.call.IncomingCallEvent
import com.example.model.call.InitiateCallRequest
import com.example.model.websocket.WsEvent
import com.example.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepositoryImpl @Inject constructor(
    private val api:       ApiService,
    private val wsManager: WebSocketManager,
) : CallRepository, BaseApiResponse() {

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _incomingCall  = MutableSharedFlow<IncomingCallEvent>(extraBufferCapacity = 1)
    private val _callAccepted  = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val _callEnded     = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        consumeWsEvents()
    }

    private fun consumeWsEvents() {

        repoScope.launch {

            wsManager.events.collect { event ->

                when (event) {

                    is WsEvent.IncomingCall -> {

                        val p = event.payload

                        _incomingCall.emit(

                            IncomingCallEvent(
                                callId = p.call_id,
                                callerId = p.caller_id,
                                callerName = p.caller_name,
                                callType = p.call_type,
                                channel = p.channel
                            )
                        )
                    }

                    is WsEvent.CallAccepted -> {
                        _callAccepted.emit(Unit)
                    }

                    is WsEvent.CallEnded -> {
                        _callEnded.emit(Unit)
                    }

                    else -> Unit
                }
            }
        }
    }

    // ── REST ──────────────────────────────────────────────────────────────────

    override suspend fun initiateCall(
        request: InitiateCallRequest,
    ): Flow<NetworkResult<CallTokenResponse>> = flow {
        emit(NetworkResult.Loading())
        emit(safeApiCall { api.initiateCall(request) })
    }.flowOn(Dispatchers.IO)

    override suspend fun acceptCall(
        request: AcceptCallRequest,
    ): Flow<NetworkResult<CallTokenResponse>> = flow {
        emit(NetworkResult.Loading())
        emit(safeApiCall { api.acceptCall(request) })
    }.flowOn(Dispatchers.IO)

    override suspend fun endCall(
        request: EndCallRequest,
    ): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        emit(safeApiCall { api.endCall(request) })
    }.flowOn(Dispatchers.IO)

    // ── WS observers — expose hot flows to the VM ─────────────────────────────

    override fun observeIncomingCalls(): Flow<IncomingCallEvent> = _incomingCall.asSharedFlow()

    override fun observeCallAccepted(): Flow<Unit> = _callAccepted.asSharedFlow()

    override fun observeCallEnded(): Flow<Unit> = _callEnded.asSharedFlow()
}