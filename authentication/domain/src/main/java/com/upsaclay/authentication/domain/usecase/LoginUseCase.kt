package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository

class LoginUseCase(
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        firebaseAuthenticationRepository.loginWithEmailAndPassword(email, password)
    }
}