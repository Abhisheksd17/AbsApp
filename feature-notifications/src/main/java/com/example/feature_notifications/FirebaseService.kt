package com.example.feature_notifications

import android.util.Log
import com.example.domain.repository.TokenRepository
import com.example.feature_notifications.NotificationConstants.TYPE_CALL_ENDED
import com.example.feature_notifications.NotificationConstants.TYPE_INCOMING_CALL
import com.example.feature_notifications.NotificationConstants.TYPE_NEW_MESSAGE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "FirebaseService"

class FirebaseService : FirebaseMessagingService() {


    @Inject
    lateinit var tokenRepository: TokenRepository


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val type = data["type"]

        Log.d(TAG, "FCM received — type=$type data=$data")

        when (type) {
            TYPE_NEW_MESSAGE -> MessageNotificationManager.show(applicationContext, data)
            TYPE_INCOMING_CALL -> CallNotificationManager.showIncomingCall(applicationContext, data)
            TYPE_CALL_ENDED -> CallNotificationManager.dismissCall(applicationContext, data)
            else -> Log.w(TAG, "Unknown FCM type: $type")
        }
    }



    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM token refreshed")
        CoroutineScope(Dispatchers.IO).launch {
            tokenRepository.register(token)
        }
    }
}