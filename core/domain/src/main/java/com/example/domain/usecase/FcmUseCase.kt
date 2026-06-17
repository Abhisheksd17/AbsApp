package com.example.domain.usecase

import com.example.domain.repository.TokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FcmUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun registerToken(token: String, userId: Int) {
        scope.launch {
            tokenRepository.register(token, userId)
        }
    }
}