package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository

class LogoutUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository,
    private val stopDataListeningUseCase: StopDataListeningUseCase,
    private val deleteLocalDataUseCase: DeleteLocalDataUseCase
) {
    suspend operator fun invoke() {
        stopDataListeningUseCase()
        deleteLocalDataUseCase()
        firebaseAuthenticationRepository.logout()
        authenticationRepository.setAuthenticated(false)
    }
}