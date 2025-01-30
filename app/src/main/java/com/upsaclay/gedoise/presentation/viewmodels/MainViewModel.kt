package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.gedoise.data.BottomNavigationItem
import com.upsaclay.gedoise.data.BottomNavigationItemType
import com.upsaclay.gedoise.domain.usecase.StartDataListeningUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val startDataListeningUseCase: StartDataListeningUseCase
) : ViewModel() {
    val bottomNavigationItem: Map<BottomNavigationItemType, BottomNavigationItem> = mapOf(
        BottomNavigationItemType.HOME to BottomNavigationItem.Home(),
        BottomNavigationItemType.MESSAGE to BottomNavigationItem.Message(),
//        BottomNavigationItemType.CALENDAR to BottomNavigationItem.Calendar(),
//        BottomNavigationItemType.FORUM to BottomNavigationItem.Forum()
    )
    private val _isAuthenticatedState = MutableStateFlow(AuthenticationState.IDLE)
    val authenticationState: StateFlow<AuthenticationState> = _isAuthenticatedState
    val currentUser: StateFlow<User?> = getCurrentUserUseCase()

    init {
        viewModelScope.launch {
            isUserAuthenticatedUseCase().collect { authenticated ->
                authenticated?.let {
                    _isAuthenticatedState.value = if (it) {
                        startDataListeningUseCase()
                        AuthenticationState.AUTHENTICATED
                    } else {
                        AuthenticationState.UNAUTHENTICATED
                    }
                }
            }
        }
    }
}