package com.example.domain.repository


import com.example.domain.data.NetworkResult
import com.example.model.call.AcceptCallRequest
import com.example.model.call.CallTokenResponse
import com.example.model.call.EndCallRequest
import com.example.model.call.IncomingCallEvent
import com.example.model.call.InitiateCallRequest
import com.example.model.websocket.WsEvent
import kotlinx.coroutines.flow.Flow

interface CallRepository {


    suspend fun acceptCall(request: AcceptCallRequest): Flow<NetworkResult<CallTokenResponse>>

    suspend fun endCall(request: EndCallRequest): Flow<NetworkResult<Unit>>

    fun observeIncomingCalls(): Flow<IncomingCallEvent>

    fun observeCallAccepted(): Flow<Unit>

    fun observeCallEnded(): Flow<Unit>

    suspend fun initiateCall(request: InitiateCallRequest): Flow<NetworkResult<CallTokenResponse>>

}