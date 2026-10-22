package com.example.data.repositoryImpl

import com.example.data.mapper.AiMapper
import com.example.data.wrapper.BaseApiResponse
import com.example.database.dao.AiDao
import com.example.domain.data.NetworkResult
import com.example.domain.repository.AiRepository
import com.example.model.ai.AiMessageUiModel
import com.example.model.ai.ChatMediaRequestDto
import com.example.network.AiService
import javax.inject.Inject
import com.example.model.ai.ChatRequestDto
import kotlin.collections.map

class AiRepostoryImpl @Inject constructor(
    private val apiService: AiService,
    private val aiDao: AiDao,
    private val mapper: AiMapper
): AiRepository, BaseApiResponse() {

    override suspend fun getAiMessage(conversation: String): NetworkResult<List<AiMessageUiModel>> {
        val result = aiDao.getConversation(conversation).filterNotNull()
        val uiModels = with(mapper) {
            messagedtoListToEntiryList(result)
        }
        return NetworkResult.Success(uiModels)
    }

    override suspend fun sendAiMessage(
        chatRequestDto: ChatRequestDto
    ): NetworkResult<Unit> {

        val result = safeApiCall {
            apiService.chatWithAi(chatRequestDto)
        }

        if (result is NetworkResult.Success) {
            result.data?.let { dto ->
                with(mapper) {
                    aiDao.insertMessage(dto.toMessageEntity())
                    aiDao.insertAttachments(dto.toAttachmentEntities())
                }
            }

            return NetworkResult.Success(Unit)
        }

        return NetworkResult.Error(
            result.message ?: "Network Fetch Failed"
        )
    }

    override suspend fun sendAiAttachments(chatRequest: ChatMediaRequestDto): NetworkResult<Unit> {
        val result=safeApiCall { apiService.chatWithAiMultiModal(chatRequest) }
        if(result is NetworkResult.Success){
            result.data?.let{dto->
                with(mapper) {
                    aiDao.insertMessage(dto.toMessageEntity())
                    aiDao.insertAttachments(dto.toAttachmentEntities())
                }
            }
            return NetworkResult.Success(Unit)
        }
        return NetworkResult.Error(result.message ?: "Network Fetch Failed")
    }

    override suspend fun deleteAiMessage(conversationId: String): NetworkResult<Unit> {
        val result=safeApiCall { apiService.deleteConversation(conversationId) }
        if(result is NetworkResult.Success){
            aiDao.deleteMessage(conversationId)
            return NetworkResult.Success(Unit)
        }
        return NetworkResult.Error(result.message ?: "Network Fetch Failed")
    }


    }

