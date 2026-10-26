package com.example.common.util

import android.content.Context
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import java.io.File
import java.security.MessageDigest

object Utils {

    fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload.jpg")

        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }

    fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    fun formatTime(isoTime: String?): String {
        if (isoTime.isNullOrBlank()) return ""
        return try {
            val date = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                java.util.Date.from(java.time.Instant.parse(isoTime))
            } else {
                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).parse(isoTime)
            }
            if (date != null) {
                java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(date)
            } else ""
        } catch (_: Exception) {
            ""
        }
    }

    fun formatTime(timestampMs: Long): String {
        if (timestampMs <= 0L) return ""
        return try {
            val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            sdf.format(java.util.Date(timestampMs))
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun showSnackBar(
        snackbarHostState: SnackbarHostState,
        message: String,
    ) {
        snackbarHostState.showSnackbar(
            message = message,
            withDismissAction = true
        )
    }




}