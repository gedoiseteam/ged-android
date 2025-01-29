package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: com.upsaclay.gedoise.domain.usecase.LogoutUseCase
) : ViewModel() {
    private val _screenState = MutableStateFlow(com.upsaclay.gedoise.domain.entities.ProfileScreenState.DEFAULT)
    val screenState: StateFlow<com.upsaclay.gedoise.domain.entities.ProfileScreenState> = _screenState
    val currentUser: StateFlow<User?> = getCurrentUserUseCase()

    fun logout() {
        _screenState.value = com.upsaclay.gedoise.domain.entities.ProfileScreenState.LOADING
        viewModelScope.launch {
            logoutUseCase()
            _screenState.value = com.upsaclay.gedoise.domain.entities.ProfileScreenState.LOGGED_OUT
        }
    }
}