package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.gedoise.data.BottomNavigationItem
import com.upsaclay.gedoise.data.BottomNavigationItemType
import com.upsaclay.gedoise.domain.usecase.DeleteLocalDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val startListeningDataUseCase: StartListeningDataUseCase,
    private val stopListeningDataUseCase: StopListeningDataUseCase,
    private val deleteLocalDataUseCase: DeleteLocalDataUseCase
) : ViewModel() {
    private val _isAuthenticatedState = MutableStateFlow(AuthenticationState.IDLE)
    val authenticationState: StateFlow<AuthenticationState> = _isAuthenticatedState
    val currentUser: StateFlow<User?> = getCurrentUserUseCase()
    val bottomNavigationItem: Map<BottomNavigationItemType, BottomNavigationItem> = mapOf(
        BottomNavigationItemType.HOME to BottomNavigationItem.Home(),
        BottomNavigationItemType.MESSAGE to BottomNavigationItem.Message()
    )

    init {
        viewModelScope.launch {
            isUserAuthenticatedUseCase().collect { authenticated ->
                authenticated?.let {
                    _isAuthenticatedState.value = if (it) {
                        startListeningDataUseCase()
                        AuthenticationState.AUTHENTICATED
                    } else {
                        stopListeningDataUseCase()
                        deleteLocalDataUseCase()
                        AuthenticationState.UNAUTHENTICATED
                    }
                }
            }
        }
    }
}