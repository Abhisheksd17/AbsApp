package com.example.common.util

import android.content.Context
import android.net.Uri
import java.io.File

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
}