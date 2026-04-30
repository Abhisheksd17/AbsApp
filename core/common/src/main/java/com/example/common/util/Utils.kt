package com.example.common.util

import android.content.Context
import android.net.Uri
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

    fun formatTime(isoTime: String): String {
        val instant = java.time.Instant.parse(isoTime)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
            .withZone(java.time.ZoneId.systemDefault())
        return formatter.format(instant)
    }
}