package com.example.worker
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.domain.repository.MessageRepository
import com.example.model.message.SendMessageRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class MessageSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MessageRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val chatId = inputData.getInt("chat_id", -1)
        val type = inputData.getString("type") ?: return Result.failure()
        val body = inputData.getString("body")
        val clientId = inputData.getString("client_id") ?: return Result.failure()

        val mediaId = inputData.getInt("media_id", -1)
            .takeIf { it != -1 }

        val replyToId = inputData.getInt("reply_to_id", -1)
            .takeIf { it != -1 }

        val isForwarded = inputData.getBoolean("is_forwarded", false)
        val userId = inputData.getInt("userId", 0)

        val request = SendMessageRequest(
            chat_id = chatId,
            type = type,
            body = body,
            client_id = clientId,
            media_id = mediaId,
            reply_to_id = replyToId,
            is_forwarded = isForwarded
        )

        return try {
            repository.sendMessage(request, userId)
            Result.success()
        } catch (e: IOException) {
            Result.retry()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure()
        }
    }
}

