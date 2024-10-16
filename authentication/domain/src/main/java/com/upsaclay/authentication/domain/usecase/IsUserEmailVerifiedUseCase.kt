package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository

class IsUserEmailVerifiedUseCase(
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository
) {
    operator fun invoke(): Boolean = firebaseAuthenticationRepository.isUserEmailVerified()
}