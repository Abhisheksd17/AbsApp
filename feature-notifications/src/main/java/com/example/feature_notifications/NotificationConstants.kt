package com.example.feature_notifications

object NotificationConstants {

    // ── Channel IDs ───────────────────────────────────────────────────────────
    const val CHANNEL_MESSAGES     = "channel_messages"
    const val CHANNEL_CALLS        = "channel_calls"
    const val CHANNEL_MISSED_CALLS = "channel_missed_calls"

    // ── Channel Names (shown in system settings) ──────────────────────────────
    const val CHANNEL_MESSAGES_NAME     = "Messages"
    const val CHANNEL_CALLS_NAME        = "Incoming Calls"
    const val CHANNEL_MISSED_CALLS_NAME = "Missed Calls"

    // ── Notification IDs ──────────────────────────────────────────────────────
    const val NOTIFICATION_ID_INCOMING_CALL = 1001
    const val NOTIFICATION_ID_MISSED_CALL   = 1002
    // Message notifications use chat_id as notification ID for grouping

    // ── FCM data.type values ──────────────────────────────────────────────────
    const val TYPE_NEW_MESSAGE  = "new_message"
    const val TYPE_INCOMING_CALL = "incoming_call"
    const val TYPE_CALL_ENDED   = "call_ended"

    // ── Intent actions ────────────────────────────────────────────────────────
    const val ACTION_ACCEPT_CALL  = "com.absapp.action.ACCEPT_CALL"
    const val ACTION_DECLINE_CALL = "com.absapp.action.DECLINE_CALL"
    const val ACTION_OPEN_CHAT    = "com.absapp.action.OPEN_CHAT"
    const val ACTION_DISMISS_CALL = "com.absapp.action.DISMISS_CALL"

    // ── Intent extra keys ─────────────────────────────────────────────────────
    const val EXTRA_CALL_ID           = "call_id"
    const val EXTRA_CALLER_ID         = "caller_id"
    const val EXTRA_CALLER_NAME       = "caller_name"
    const val EXTRA_CALL_TYPE         = "call_type"
    const val EXTRA_CHANNEL           = "channel"
    const val EXTRA_CALLER_AVATAR_URL = "caller_avatar_url"
    const val EXTRA_CHAT_ID           = "chat_id"
    const val SENDER_ID           = "senderId"
    const val EXTRA_MESSAGE_ID        = "message_id"
    const val EXTRA_SENDER_NAME       = "sender_name"
    const val EXTRA_BODY_PREVIEW      = "body_preview"
    const val EXTRA_MSG_TYPE          = "msg_type"
    const val EXTRA_SENDER_AVATAR_URL = "sender_avatar_url"
    const val EXTRA_REASON            = "reason"

    // ── Call reason values ────────────────────────────────────────────────────
    const val REASON_ENDED   = "ended"
    const val REASON_REJECTED = "rejected"
    const val REASON_MISSED  = "missed"

    // ── Notification group keys ───────────────────────────────────────────────
    const val GROUP_MESSAGES = "group_messages"

    // ── Request codes (unique per PendingIntent) ──────────────────────────────
    const val RC_OPEN_CHAT       = 2001
    const val RC_ACCEPT_CALL     = 2002
    const val RC_DECLINE_CALL    = 2003
    const val RC_FULL_SCREEN     = 2004
    const val RC_DISMISS_CALL    = 2005
}