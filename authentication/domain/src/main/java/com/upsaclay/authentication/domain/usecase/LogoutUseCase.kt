package com.upsaclay.authentication.domain.usecase

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository
import com.upsaclay.common.domain.repository.UserRepository

class LogoutUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        firebaseAuthenticationRepository.logout()
        userRepository.removeCurrentUser()
        authenticationRepository.setAuthenticated(false)
    }
}