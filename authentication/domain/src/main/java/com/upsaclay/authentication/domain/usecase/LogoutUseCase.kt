package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.AuthenticationRepository

class LogoutUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke() {
        authenticationRepository.logout()
    }
}