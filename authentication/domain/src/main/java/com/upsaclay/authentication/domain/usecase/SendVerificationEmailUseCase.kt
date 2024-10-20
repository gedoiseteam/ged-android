package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository

class SendVerificationEmailUseCase(
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository
) {
    suspend fun sendVerificationEmail(): Result<Unit> {
        return firebaseAuthenticationRepository.sendVerificationEmail()
    }
}