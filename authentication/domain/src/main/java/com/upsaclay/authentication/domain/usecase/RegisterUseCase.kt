package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository

class RegisterUseCase(
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): String =
        firebaseAuthenticationRepository.registerWithEmailAndPassword(email, password)
}