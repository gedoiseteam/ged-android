package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    val currentUser: StateFlow<User?> = userRepository.currentUser

    fun logout() {
        viewModelScope.launch {
            authenticationRepository.logout()
        }
    }
}