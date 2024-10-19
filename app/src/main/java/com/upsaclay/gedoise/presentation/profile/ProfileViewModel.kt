package com.upsaclay.gedoise.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.domain.usecase.LogoutUseCase
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GetCurrentUserFlowUseCase
import com.upsaclay.gedoise.data.profile.ProfileState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    getCurrentUserFlowUseCase: GetCurrentUserFlowUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileState.DEFAULT)
    val profileState: StateFlow<ProfileState> = _profileState
    val user: Flow<User> = getCurrentUserFlowUseCase()

    fun logout() {
        _profileState.value = ProfileState.LOADING
        viewModelScope.launch {
            logoutUseCase()
            delay(1000)
            _profileState.value = ProfileState.LOGGED_OUT
        }
    }
}