package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationEvent
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.gedoise.data.NavigationItem
import com.upsaclay.gedoise.domain.usecase.ClearDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import com.upsaclay.message.domain.usecase.GetAllLastUnreadMessagesReceivedUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

class MainViewModel(
    private val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val startListeningDataUseCase: StartListeningDataUseCase,
    private val stopListeningDataUseCase: StopListeningDataUseCase,
    private val clearDataUseCase: ClearDataUseCase,
    private val getAllLastUnreadMessagesReceivedUseCase: GetAllLastUnreadMessagesReceivedUseCase
): ViewModel() {
    private val _authenticationState = MutableStateFlow(AuthenticationState.WAITING)
    val authenticationState: StateFlow<AuthenticationState> = _authenticationState

    private val _homeNavigationItem = MutableStateFlow(NavigationItem.Home())
    val homeNavigationItem: Flow<NavigationItem> = _homeNavigationItem

    private val _messageNavigationItem = MutableStateFlow(NavigationItem.Message())
    val messageNavigationItem: Flow<NavigationItem> = _messageNavigationItem

    val currentUser: StateFlow<User?> = getCurrentUserUseCase()

    init {
        listenAuthenticationState()
        updateMessageNavigationItemBadges()
    }

    private fun listenAuthenticationState() {
        isUserAuthenticatedUseCase()
            .filterNotNull()
            .map {
                if (it) {
                    startListeningDataUseCase()
                    _authenticationState.value = AuthenticationState.AUTHENTICATED
                } else {
                    stopListeningDataUseCase()
                    _authenticationState.value = AuthenticationState.UNAUTHENTICATED
                    delay(2000)
                    clearDataUseCase()
                }
            }.launchIn(viewModelScope)
    }

    private fun updateMessageNavigationItemBadges() {
        currentUser
            .filterNotNull()
            .distinctUntilChangedBy { it.id }
            .map { currentUser ->
                getAllLastUnreadMessagesReceivedUseCase(currentUser.id)
                    .collect { messages ->
                        _messageNavigationItem.value = NavigationItem.Message(badges = messages.size)
                    }
            }.launchIn(viewModelScope)
    }
}