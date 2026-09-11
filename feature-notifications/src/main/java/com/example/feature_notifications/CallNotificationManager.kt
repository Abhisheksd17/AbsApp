package com.example.feature_notifications

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.feature_notifications.NotificationConstants.CHANNEL_CALLS
import com.example.feature_notifications.NotificationConstants.ACTION_ACCEPT_CALL
import com.example.feature_notifications.NotificationConstants.ACTION_DECLINE_CALL
import com.example.feature_notifications.NotificationConstants.ACTION_DISMISS_CALL
import com.example.feature_notifications.NotificationConstants.CHANNEL_MISSED_CALLS
import com.example.feature_notifications.NotificationConstants.EXTRA_CALL_ID
import com.example.feature_notifications.NotificationConstants.EXTRA_CALL_TYPE
import com.example.feature_notifications.NotificationConstants.EXTRA_CALLER_AVATAR_URL
import com.example.feature_notifications.NotificationConstants.EXTRA_CALLER_ID
import com.example.feature_notifications.NotificationConstants.EXTRA_CALLER_NAME
import com.example.feature_notifications.NotificationConstants.EXTRA_CHANNEL
import com.example.feature_notifications.NotificationConstants.NOTIFICATION_ID_INCOMING_CALL
import com.example.feature_notifications.NotificationConstants.NOTIFICATION_ID_MISSED_CALL
import com.example.feature_notifications.NotificationConstants.RC_ACCEPT_CALL
import com.example.feature_notifications.NotificationConstants.RC_DECLINE_CALL
import com.example.feature_notifications.NotificationConstants.RC_DISMISS_CALL
import com.example.feature_notifications.NotificationConstants.RC_FULL_SCREEN
import com.example.feature_notifications.NotificationConstants.REASON_MISSED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


object CallNotificationManager {

    private var ringtone: Ringtone? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    fun showIncomingCall(context: Context, data: Map<String, String?>) {
        val callId      = data["call_id"]          ?: return
        val callerId    = data["caller_id"]        ?: return
        val callerName  = data["caller_name"].orEmpty()
        val callType    = data["call_type"]        ?: "audio"
        val channel     = data["channel"]          ?: return
        val avatarUrl   = data["caller_avatar_url"]

        scope.launch {
            val avatar = NotificationHelper.loadAvatar(context,avatarUrl)

            val callTypeLabel = if (callType == "video") "Video call" else "Voice call"
            val extras = mapOf(
                "type" to "call",
                EXTRA_CALL_ID           to callId,
                EXTRA_CALLER_ID         to callerId,
                EXTRA_CALLER_NAME       to callerName,
                EXTRA_CALL_TYPE         to callType,
                EXTRA_CHANNEL           to channel,
                EXTRA_CALLER_AVATAR_URL to (avatarUrl ?: ""),
            )

            val fullScreenIntent = NotificationHelper.contentIntent(
                context      = context,
                targetClass = NotificationActivityProvider.getMainActivity(),
                extras       = extras,
                requestCode  = RC_FULL_SCREEN,
            )

            val acceptIntent = NotificationHelper.broadcastIntent(
                context     = context,
                action      = ACTION_ACCEPT_CALL,
                extras      = extras,
                requestCode = RC_ACCEPT_CALL,
            )

            val declineIntent = NotificationHelper.broadcastIntent(
                context     = context,
                action      = ACTION_DECLINE_CALL,
                extras      = extras,
                requestCode = RC_DECLINE_CALL,
            )

            val builder = NotificationHelper.baseBuilder(context, CHANNEL_CALLS)
                .setContentTitle(callerName)
                .setContentText(callTypeLabel)
                .setOngoing(true)
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(fullScreenIntent, true)
                .setContentIntent(fullScreenIntent)
                .apply {
                    avatar?.let { setLargeIcon(it) }

                    addAction(
                        NotificationCompat.Action.Builder(
                            com.example.ui.R.drawable.calling_ic,
                            "Accept",
                            acceptIntent,
                        ).build()
                    )

                    addAction(
                        NotificationCompat.Action.Builder(
                            com.example.ui.R.drawable.call_declined_ic,
                            "Decline",
                            declineIntent,
                        ).build()
                    )
                }

            NotificationHelper.notify(context, NOTIFICATION_ID_INCOMING_CALL, builder)
            startRingtone(context)
        }
    }


    fun dismissCall(
        context: Context,
        data: Map<String, String?>,
    ) {
        val callId      = data["call_id"]          ?: return
        val reason      = data["reason"]           ?: NotificationConstants.REASON_ENDED
        val callerName  = data["caller_name"].orEmpty()
        stopRingtone()
        NotificationHelper.cancel(context, NOTIFICATION_ID_INCOMING_CALL)

        if (reason == REASON_MISSED) {
            showMissedCall(context, callId, callerName)
        }
    }


    fun dismissIncomingCallNotification(context: Context) {
        stopRingtone()
        NotificationHelper.cancel(context, NOTIFICATION_ID_INCOMING_CALL)
    }


    private fun showMissedCall(
        context: Context,
        callId: String,
        callerName: String,
    ) {
        val dismissIntent = NotificationHelper.broadcastIntent(
            context     = context,
            action      = ACTION_DISMISS_CALL,
            extras      = mapOf(EXTRA_CALL_ID to callId),
            requestCode = RC_DISMISS_CALL,
        )

        val builder = NotificationHelper.baseBuilder(context, CHANNEL_MISSED_CALLS)
            .setContentTitle("Missed call")
            .setContentText(callerName)
            .setSmallIcon(com.example.ui.R.drawable.call_missed_ic)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MISSED_CALL)
            .setDeleteIntent(dismissIntent)

        NotificationHelper.notify(context, NOTIFICATION_ID_MISSED_CALL, builder)
    }


    private fun startRingtone(context: Context) {
        runCatching {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(context.applicationContext, uri)?.also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    it.isLooping = true
                }
                it.audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                it.play()
            }
        }
    }

    private fun stopRingtone() {
        runCatching { ringtone?.stop() }
        ringtone = null
    }
}