package com.example.data.repositoryImpl

import android.util.Log
import com.example.data.mapper.AiMapper
import com.example.data.wrapper.BaseApiResponse
import com.example.database.dao.AiDao
import com.example.database.entity.AiMessageEntity
import com.example.domain.data.NetworkResult
import com.example.domain.repository.AiRepository
import com.example.model.ai.AiConversationSummary
import com.example.model.ai.AiMessageUiModel
import com.example.model.ai.ChatMediaRequestDto
import com.example.model.ai.ChatRequestDto
import com.example.model.ai.ChatResponse
import com.example.network.AiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "AiRepositoryImpl"

class AiRepostoryImpl @Inject constructor(
    private val apiService: AiService,
    private val aiDao: AiDao,
    private val mapper: AiMapper
): AiRepository, BaseApiResponse() {


    override fun getAllConversations(): Flow<List<AiConversationSummary>> {
        return aiDao.getAllConversations()
    }

    override suspend fun togglePinConversation(conversationId: String, isPinned: Boolean) {
        aiDao.updatePinStatus(conversationId, isPinned)
    }

    override fun getAiMessage(conversation: String): Flow<NetworkResult<List<AiMessageUiModel>>> {
        return aiDao.getConversationMessage(conversation)
            .map { list ->
                val uiModels = mapper.entityListToUiModelList(list)
                NetworkResult.Success(uiModels) as NetworkResult<List<AiMessageUiModel>>
            }
            .catch { e ->
                emit(NetworkResult.Error(e.message ?: "Unknown error"))
            }
    }

    override suspend fun sendAiMessage(
        chatRequestDto: ChatRequestDto
    ): NetworkResult<ChatResponse> {

        val conversationId = chatRequestDto.conversationId ?: ""

            with(mapper) {
                val userMsg = chatRequestDto.toUserMessageEntity()
                aiDao.insertMessageResponse(userMsg)
            }

            val result = safeApiCall {
                apiService.chatWithAi(chatRequestDto)
            }

        if (result is NetworkResult.Success) {
            result.data?.let { dto ->
                with(mapper) {
                    aiDao.insertMessageResponse(dto.toMessageEntity())
                    aiDao.insertAttachmentsResponse(dto.toAttachmentEntities())
                }
                return NetworkResult.Success(dto)
            }
        }
        val errorMessage = result.message ?: "Network Fetch Failed"
        aiDao.insertMessageResponse(
            AiMessageEntity(
                messageId = java.util.UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = "assistant",
                message = "⚠️ Error: $errorMessage",
                status = "ERROR"
            )
        )
        return NetworkResult.Error(errorMessage)

    }

    override suspend fun sendAiAttachments(chatRequest: ChatMediaRequestDto): NetworkResult<ChatResponse> {
        val conversationId = chatRequest.conversationId ?: ""
        with(mapper) {
            val userMsgId = java.util.UUID.randomUUID().toString()
            val userMsg = chatRequest.toUserMessageEntity(userMsgId)
            val userAttachments = chatRequest.toUserAttachmentEntities(userMsgId)
            aiDao.insertMessageResponse(userMsg)
            if (userAttachments.isNotEmpty()) {
                aiDao.insertAttachmentsResponse(userAttachments)
            }
        }

        val conversationIdBody = chatRequest.conversationId?.takeIf { it.isNotBlank() }
            ?.toRequestBody("text/plain".toMediaTypeOrNull())

        val messageBody = chatRequest.message?.takeIf { it.isNotBlank() }
            ?.toRequestBody("text/plain".toMediaTypeOrNull())

        val fileParts = chatRequest.files.map { file ->
            val mimeType = getMimeType(file)
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("files", file.name, requestBody)
        }.takeIf { it.isNotEmpty() }

        val result = safeApiCall {
            apiService.chatWithAiMultiModal(
                conversationId = conversationIdBody,
                message = messageBody,
                files = fileParts
            )
        }
        if (result is NetworkResult.Success) {
            result.data?.let { dto ->
                with(mapper) {
                    aiDao.insertMessageResponse(dto.toMessageEntity())
                    aiDao.insertAttachmentsResponse(dto.toAttachmentEntities())
                }
                return NetworkResult.Success(dto)
            }
        }
        val errorMessage = result.message ?: "Network Fetch Failed"
        aiDao.insertMessageResponse(
            AiMessageEntity(
                messageId = java.util.UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = "assistant",
                message = "⚠️ Error: $errorMessage",
                status = "ERROR"
            )
        )
        return NetworkResult.Error(errorMessage)
    }

    private fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            "mp4" -> "video/mp4"
            "mp3" -> "audio/mpeg"
            "pdf" -> "application/pdf"
            "txt" -> "text/plain"
            else -> "application/octet-stream"
        }
    }

    override suspend fun deleteAiMessage(conversationId: String): NetworkResult<Unit> {
        val result = safeApiCall { apiService.deleteConversation(conversationId) }
        if (result is NetworkResult.Success) {
            aiDao.deleteMessage(conversationId)
            return NetworkResult.Success(Unit)
        }
        return NetworkResult.Error(result.message ?: "Network Fetch Failed")
    }


}

