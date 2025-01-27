package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository

class SendVerificationEmailUseCase(
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository
) {
    suspend operator fun invoke() {
        firebaseAuthenticationRepository.sendVerificationEmail()
    }
}