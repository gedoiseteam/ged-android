package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository

class RegisterUseCase(
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return firebaseAuthenticationRepository.registerWithEmailAndPassword(email, password)
    }
}