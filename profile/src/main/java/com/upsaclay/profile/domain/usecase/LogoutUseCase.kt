package com.upsaclay.profile.domain.usecase

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository

class LogoutUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val conversationRepository: UserConversationRepository
) {
    suspend operator fun invoke() {
        messageRepository.deleteLocalMessages()
        conversationRepository.deleteLocalConversations()
        userRepository.removeCurrentUser()
        firebaseAuthenticationRepository.logout()
        authenticationRepository.setAuthenticated(false)
    }
}