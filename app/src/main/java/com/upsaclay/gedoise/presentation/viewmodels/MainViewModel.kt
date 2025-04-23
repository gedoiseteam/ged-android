package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.gedoise.domain.usecase.ClearDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.replay
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val startListeningDataUseCase: StartListeningDataUseCase,
    private val stopListeningDataUseCase: StopListeningDataUseCase,
    private val clearDataUseCase: ClearDataUseCase
): ViewModel() {
    fun startListening() {
        updateDataListening()
        checkCurrentUser()
    }

    private fun updateDataListening() {
        viewModelScope.launch {
            authenticationRepository.isAuthenticated
                .filterNotNull()
                .collectLatest {
                    if (it) {
                        startListeningDataUseCase()
                    } else {
                        stopListeningDataUseCase()
                        delay(2000)
                        clearDataUseCase()
                    }
                }
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            userRepository.currentUser.filterNotNull()
                .take(1)
                .collect { currentUser ->
                    userRepository.getUser(currentUser.id)?.let { remoteUser ->
                        if (remoteUser != currentUser) {
                            userRepository.setCurrentUser(remoteUser)
                        }
                    } ?: run {
                        authenticationRepository.logout()
                        userRepository.deleteCurrentUser()
                    }
                }
        }
    }
}