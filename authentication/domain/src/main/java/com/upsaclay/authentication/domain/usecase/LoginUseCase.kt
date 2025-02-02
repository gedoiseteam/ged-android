package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.AuthenticationRepository

class LoginUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        authenticationRepository.loginWithEmailAndPassword(email, password)
    }
}