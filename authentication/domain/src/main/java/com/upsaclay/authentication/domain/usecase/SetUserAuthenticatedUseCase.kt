package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.AuthenticationRepository

class SetUserAuthenticatedUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(isAuthenticated: Boolean) {
        authenticationRepository.setAuthenticated(isAuthenticated)
    }
}