package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.AuthenticationRepository

class SendVerificationEmailUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke() {
        authenticationRepository.sendVerificationEmail()
    }
}