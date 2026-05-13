package com.example.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.data.CallState
import com.example.domain.data.NetworkResult
import com.example.domain.repository.CallRepository
import com.example.model.call.AcceptCallRequest
import com.example.model.call.CallParams
import com.example.model.call.CallTokenResponse
import com.example.model.call.EndCallRequest
import com.example.model.call.IncomingCallEvent
import com.example.model.call.InitiateCallRequest
import com.example.model.websocket.WsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallerViewModel @Inject constructor(
    private val repository: CallRepository
) : ViewModel() {


    init {
        observeIncomingCalls()
        observeCallAccepted()
        observeCallEnded()
    }
    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState: StateFlow<CallState> = _callState.asStateFlow()


    fun initiateCall(calleeId: Int, calleeName: String, isVideo: Boolean = true) {
        viewModelScope.launch {
            repository.initiateCall(
                InitiateCallRequest(
                    callee_id = calleeId,
                    call_type = if (isVideo) "video" else "audio",
                )
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _callState.value = CallState.Loading
                    is NetworkResult.Success -> _callState.value = CallState.Ringing(
                        result.data!!.toCallParams(
                            peerId   = calleeId,
                            peerName = calleeName,
                            isVideo  = isVideo,
                            isCaller = true,
                        )
                    )
                    is NetworkResult.Error -> _callState.value = CallState.Error(
                        result.message ?: "Failed to initiate call"
                    )
                    else -> Unit
                }
            }
        }
    }


    // ── Callee ────────────────────────────────────────────────────────────────

    fun acceptCall(event: IncomingCallEvent) {
        viewModelScope.launch {
            repository.acceptCall(
                AcceptCallRequest(
                    caller_id = event.callerId,
                    call_id   = event.callId,
                )
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _callState.value = CallState.Loading
                    is NetworkResult.Success -> _callState.value = CallState.Active(
                        result.data!!.toCallParams(
                            peerId   = event.callerId,
                            peerName = event.callerName,
                            isVideo  = event.callType == "video",
                            isCaller = false,
                        )
                    )
                    is NetworkResult.Error -> _callState.value = CallState.Error(
                        result.message ?: "Failed to accept call"
                    )
                    else -> Unit
                }
            }
        }
    }

    // ── Either side ───────────────────────────────────────────────────────────

    fun notifyCallEnded(peerId: Int, callId: String, reason: String = "ended") {
        viewModelScope.launch {
            repository.endCall(
                EndCallRequest(peer_id = peerId, call_id = callId, reason = reason)
            ).collect { }
            _callState.value = CallState.Idle
        }
    }

    fun rejectCall(event: IncomingCallEvent) =
        notifyCallEnded(event.callerId, event.callId, reason = "rejected")

    fun resetState() {
        _callState.value = CallState.Idle
    }

    // ── WS observers (repo already filtered + mapped) ─────────────────────────

    private fun observeIncomingCalls() {
        viewModelScope.launch {
            repository.observeIncomingCalls().collect { incoming ->
                _callState.value = CallState.Incoming(incoming)
            }
        }
    }

    private fun observeCallAccepted() {
        viewModelScope.launch {
            repository.observeCallAccepted().collect {
                // Caller: Ringing → Active (reuse same params, Agora already joined)
                val ringing = _callState.value as? CallState.Ringing ?: return@collect
                _callState.value = CallState.Active(ringing.params)
            }
        }
    }

    private fun observeCallEnded() {
        viewModelScope.launch {
            repository.observeCallEnded().collect {
                _callState.value = CallState.Idle
            }
        }
    }
}



private fun CallTokenResponse.toCallParams(
    peerId:   Int,
    peerName: String,
    isVideo:  Boolean,
    isCaller: Boolean,
) = CallParams(
    appId    = app_id,
    channel  = channel,
    token    = token,
    uid      = uid,
    callId   = call_id,
    peerId   = peerId,
    peerName = peerName,
    isVideo  = isVideo,
    isCaller = isCaller,
)