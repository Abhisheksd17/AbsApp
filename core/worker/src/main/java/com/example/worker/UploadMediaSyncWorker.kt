package com.example.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.domain.repository.MessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

@HiltWorker
class UploadMediaSyncWorker@AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MessageRepository
) :CoroutineWorker(appContext, workerParams){
    override suspend fun doWork(): Result {
        val uriString = inputData.getString("uri")
            ?: return Result.failure()

        val chatId = inputData.getInt("chatId", -1)
        if (chatId == -1) return Result.failure()

        val type = inputData.getString("type")
            ?: return Result.failure()

        val uri = Uri.parse(uriString)

        return try {
            repository.uploadMedia(uri, chatId, type)
            Result.success()
        } catch (e: IOException) {
            Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}