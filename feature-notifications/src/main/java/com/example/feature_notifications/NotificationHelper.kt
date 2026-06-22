package com.example.feature_notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.ImageLoader
import com.example.feature_notifications.NotificationConstants.CHANNEL_CALLS
import com.example.feature_notifications.NotificationConstants.CHANNEL_CALLS_NAME
import com.example.feature_notifications.NotificationConstants.CHANNEL_MESSAGES
import com.example.feature_notifications.NotificationConstants.CHANNEL_MESSAGES_NAME
import com.example.feature_notifications.NotificationConstants.CHANNEL_MISSED_CALLS
import com.example.feature_notifications.NotificationConstants.CHANNEL_MISSED_CALLS_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.ui.R
import coil.request.ImageRequest

object NotificationHelper {


    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_MESSAGES,
                CHANNEL_MESSAGES_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "New chat messages"
                enableVibration(true)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_CALLS,
                CHANNEL_CALLS_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Incoming voice and video calls"
                enableVibration(true)
                setBypassDnd(true)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_MISSED_CALLS,
                CHANNEL_MISSED_CALLS_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Missed calls"
            }
        )
    }


    fun baseBuilder(
        context: Context,
        channelId: String,
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_ic)   // monochrome, 24 dp
            .setAutoCancel(true)
            .setLocalOnly(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)


    /**
     * Tapping a notification opens the target Activity with the supplied extras.
     */
    fun contentIntent(
        context: Context,
        targetClass: Class<*>,
        extras: Map<String, String> = emptyMap(),
        requestCode: Int,
    ): PendingIntent {
        val intent = Intent(context, targetClass).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            extras.forEach { (k, v) -> putExtra(k, v) }
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            pendingIntentFlags(),
        )
    }

    /**
     * Action button or broadcast-based intent (used for call accept / decline).
     */
    fun broadcastIntent(
        context: Context,
        action: String,
        extras: Map<String, String> = emptyMap(),
        requestCode: Int,
    ): PendingIntent {
        val intent = Intent(action).apply {
            setPackage(context.packageName)
            extras.forEach { (k, v) -> putExtra(k, v) }
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            pendingIntentFlags(),
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Show / cancel
    // ─────────────────────────────────────────────────────────────────────────

    fun notify(context: Context, id: Int, builder: NotificationCompat.Builder) {
        NotificationManagerCompat.from(context).notify(id, builder.build())
    }

    fun cancel(context: Context, id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }

    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Avatar loader — returns null on any failure (never throws)
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun loadAvatar(
        context: Context,
        url: String?
    ): Bitmap? {

        if (url.isNullOrBlank()) return null

        return withContext(Dispatchers.IO) {
            runCatching {
                val loader = ImageLoader(context)

                val request = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .build()

                val result = loader.execute(request)

                (result.drawable as? BitmapDrawable)?.bitmap
            }.getOrNull()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal
    // ─────────────────────────────────────────────────────────────────────────

    private fun pendingIntentFlags(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_UPDATE_CURRENT
}