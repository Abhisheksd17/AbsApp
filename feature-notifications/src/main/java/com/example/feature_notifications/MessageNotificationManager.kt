package com.example.feature_notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.example.feature_notifications.NotificationConstants.EXTRA_CHAT_ID
import com.example.feature_notifications.NotificationConstants.EXTRA_SENDER_AVATAR_URL
import com.example.feature_notifications.NotificationConstants.CHANNEL_MESSAGES
import com.example.feature_notifications.NotificationConstants.EXTRA_SENDER_NAME
import com.example.feature_notifications.NotificationConstants.GROUP_MESSAGES
import com.example.feature_notifications.NotificationConstants.RC_OPEN_CHAT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.common.R


object MessageNotificationManager {


    private val pendingMessages = mutableMapOf<Int, MutableList<PendingMessage>>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)




    fun show(context: Context, data: Map<String, String?>) {
        val chatId      = data["chat_id"]?.toIntOrNull()     ?: return
        val messageId   = data["message_id"]?.toIntOrNull()  ?: return
        val senderName  = data["sender_name"].orEmpty()
        val bodyPreview = data["body_preview"].orEmpty()
        val avatarUrl   = data["sender_avatar_url"]

        scope.launch {
            val avatar = NotificationHelper.loadAvatar(context, avatarUrl)

            val list = pendingMessages.getOrPut(chatId) { mutableListOf() }
            list.add(PendingMessage(senderName, bodyPreview, System.currentTimeMillis()))

            showConversationNotification(context, chatId, senderName, avatarUrl, list, avatar?.let { IconCompat.createWithBitmap(it) })

            if (pendingMessages.size > 1) {
                showGroupSummary(context)
            }
        }
    }


    fun clearChat(context: Context, chatId: Int) {
        pendingMessages.remove(chatId)
        NotificationHelper.cancel(context, chatId)

        if (pendingMessages.size <= 1) {
            NotificationHelper.cancel(context, SUMMARY_ID)
        }
    }


    fun clearAll(context: Context) {
        pendingMessages.clear()
        NotificationHelper.cancelAll(context)
    }


    private fun showConversationNotification(
        context: Context,
        chatId: Int,
        senderName: String,
        avatarUrl: String?,
        messages: List<PendingMessage>,
        senderIcon: IconCompat?,
    ) {
        val me = Person.Builder()
            .setName(context.getString(R.string.notification_you))
            .build()

        val sender = Person.Builder()
            .setName(senderName)
            .apply { senderIcon?.let { setIcon(it) } }
            .build()

        val style = NotificationCompat.MessagingStyle(me)

        messages.forEach { msg ->

            style.addMessage(
                NotificationCompat.MessagingStyle.Message(
                    msg.text,
                    msg.timestamp,
                    sender
                )
            )
        }

        val tapIntent = NotificationHelper.contentIntent(
            context = context,
            targetClass = NotificationActivityProvider.getMainActivity(),
            extras = mapOf(
                "type" to "chat",
                EXTRA_CHAT_ID to chatId.toString(),
                EXTRA_SENDER_NAME to senderName,
                EXTRA_SENDER_AVATAR_URL to (avatarUrl ?: ""),
            ),
            requestCode = RC_OPEN_CHAT + chatId,
        )

        val builder = NotificationHelper.baseBuilder(context, CHANNEL_MESSAGES)
            .setStyle(style)
            .setContentTitle(senderName)
            .setContentText(messages.last().text)
            .setNumber(messages.size)
            .setContentIntent(tapIntent)
            .setGroup(GROUP_MESSAGES)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationHelper.notify(context, chatId, builder)
    }


    private fun showGroupSummary(context: Context) {
        val totalUnread   = pendingMessages.values.sumOf { it.size }
        val chatCount     = pendingMessages.size
        val summaryText   = context.getString(
            R.string.notification_summary,
            totalUnread,
            chatCount,
        )

        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(summaryText)

        pendingMessages.entries.forEach { (_, msgs) ->
            inboxStyle.addLine(msgs.last().text)
        }

        val builder = NotificationHelper.baseBuilder(context, CHANNEL_MESSAGES)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(summaryText)
            .setStyle(inboxStyle)
            .setGroup(GROUP_MESSAGES)
            .setGroupSummary(true)
            .setAutoCancel(true)

        NotificationHelper.notify(context, SUMMARY_ID, builder)
    }


    private const val SUMMARY_ID = 9999

    private data class PendingMessage(
        val senderName: String,
        val text: String,
        val timestamp: Long,
    )
}