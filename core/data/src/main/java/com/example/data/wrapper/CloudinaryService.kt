package com.example.data.wrapper

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.example.model.media.UploadResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryService @Inject constructor(
    @ApplicationContext private val context: Context
) {



    /**
     * Upload any media (image / video / audio) using unsigned preset
     */
    suspend fun upload(
        context: Context,
        uri: Uri
    ): UploadResult = withContext(Dispatchers.IO) {

        val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val bytes = getSize(context, uri)
        val sha256 = computeSha256(context, uri)

        val secureUrl = suspendCancellableCoroutine<String> { cont ->

            MediaManager.get().upload(uri)
                .unsigned("instagram_upload") // ← your preset
                .option("resource_type", "auto") // supports image/video/audio
                .callback(object : com.cloudinary.android.callback.UploadCallback {

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("secure_url") as? String
                        if (url != null) {
                            cont.resume(url)
                        } else {
                            cont.resumeWithException(Exception("Missing secure_url"))
                        }
                    }

                    override fun onError(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                        cont.resumeWithException(Exception(error?.description))
                    }

                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onReschedule(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {}
                })
                .dispatch()
        }

        UploadResult(
            secureUrl = secureUrl,
            bytes = bytes,
            mimeType = mime,
            sha256 = sha256
        )
    }

    // ─────────────────────────────────────────────

    private fun getSize(context: Context, uri: Uri): Long {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex != -1) {
                return cursor.getLong(sizeIndex)
            }
        }
        return 0
    }

    private fun computeSha256(context: Context, uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return ""

        inputStream.use { stream ->
            val buffer = ByteArray(8192)
            var read: Int
            while (stream.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }
        }

        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}