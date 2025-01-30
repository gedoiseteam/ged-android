package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.gedoise.domain.entities.ProfileScreenState
import com.upsaclay.gedoise.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _screenState = MutableStateFlow(ProfileScreenState.DEFAULT)
    val screenState: StateFlow<ProfileScreenState> = _screenState
    val currentUser: StateFlow<User?> = getCurrentUserUseCase()

    fun logout() {
        _screenState.value = ProfileScreenState.LOADING
        viewModelScope.launch {
            logoutUseCase()
            _screenState.value = ProfileScreenState.LOGGED_OUT
        }
    }
}