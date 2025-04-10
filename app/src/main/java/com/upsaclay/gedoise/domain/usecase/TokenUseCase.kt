package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.ConnectivityObserver
import com.upsaclay.gedoise.domain.entities.FcmToken
import com.upsaclay.gedoise.domain.repository.CredentialsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class TokenUseCase(
    private val credentialsRepository: CredentialsRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val scope: CoroutineScope
) {
    fun listenEvents() {
        scope.launch {
            combine(
                authenticationRepository.isAuthenticated.filterNotNull(),
                connectivityObserver.isConnected,
                credentialsRepository.fcmToken.filterNotNull()
            ) { isAuthenticated, _, fcmToken ->
                Pair(isAuthenticated, fcmToken)
            }.collectLatest { (isAuthenticated, fcmToken) ->
                if (isAuthenticated) {
                    updateToken(fcmToken)
                } else {
                    credentialsRepository.removeUnsentFcmToken()
                }
            }
        }
    }

    private suspend fun updateToken(fcmToken: FcmToken) {
        runCatching {
            credentialsRepository.sendFcmToken(fcmToken)
            credentialsRepository.removeUnsentFcmToken()
        }.onFailure {
            e("Error sending FCM token", it)
        }
    }
}