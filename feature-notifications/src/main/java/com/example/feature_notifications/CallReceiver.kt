package com.example.feature_notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.domain.data.CallState
import com.example.domain.repository.CallRepository
import com.example.feature_notifications.NotificationConstants.ACTION_ACCEPT_CALL
import com.example.feature_notifications.NotificationConstants.ACTION_DECLINE_CALL
import com.example.feature_notifications.NotificationConstants.EXTRA_CALL_ID
import com.example.feature_notifications.NotificationConstants.EXTRA_CALLER_ID
import com.example.model.call.AcceptCallRequest
import com.example.model.call.EndCallRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var callRepository: CallRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val callId = intent.getStringExtra(EXTRA_CALL_ID) ?: return
        val callerId = intent.getStringExtra(EXTRA_CALLER_ID)?.toIntOrNull() ?: 0


        CallNotificationManager.dismissIncomingCallNotification(context)

        val pendingResult = goAsync()



        scope.launch {
            try {
                when (action) {
                    ACTION_ACCEPT_CALL -> {
                        callRepository.acceptCall(AcceptCallRequest(callerId, callId)).collect { result ->
                        }
                        CallNotificationManager.dismissIncomingCallNotification(context)
                        
                        val startIntent = NotificationActivityProvider.getMainActivity().let {
                            Intent(context, it).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                putExtras(intent.extras ?: android.os.Bundle())
                            }
                        }
                        context.startActivity(startIntent)
                    }

                    ACTION_DECLINE_CALL -> {
                        callRepository.endCall(EndCallRequest(callerId, callId, "rejected")).collect { result ->
                            CallNotificationManager.dismissIncomingCallNotification(context)
                        }
                    }
                }
            } catch (e: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }
}