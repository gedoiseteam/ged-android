package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.gedoise.data.ScreenRepository
import com.upsaclay.gedoise.domain.usecase.ClearDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import com.upsaclay.gedoise.presentation.NavigationItem
import com.upsaclay.message.domain.entity.MessageScreen
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NavigationViewModel(
    private val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val startListeningDataUseCase: StartListeningDataUseCase,
    private val stopListeningDataUseCase: StopListeningDataUseCase,
    private val clearDataUseCase: ClearDataUseCase,
    private val userConversationRepository: UserConversationRepository,
    private val screenRepository: ScreenRepository
): ViewModel() {
    private val _authenticationState = MutableStateFlow(AuthenticationState.WAITING)
    val authenticationState: StateFlow<AuthenticationState> = _authenticationState

    private val _routeToNavigate = MutableSharedFlow<String>()
    val routeToNavigate: Flow<String> = _routeToNavigate

    private val _homeNavigationItem = MutableStateFlow(NavigationItem.Home())
    val homeNavigationItem: Flow<NavigationItem> = _homeNavigationItem

    private val _messageNavigationItem = MutableStateFlow(NavigationItem.Message())
    val messageNavigationItem: Flow<NavigationItem> = _messageNavigationItem

    val currentUser: StateFlow<User?> = getCurrentUserUseCase()

    init {
        listenAuthenticationState()
        updateMessageNavigationItemBadges()
    }

    fun navigateTo(screen: Screen) {
        val route = when(screen) {
            is MessageScreen.Chat -> MessageScreen.Chat(screen.conversation).route
            else -> return
        }
        viewModelScope.launch {
            _routeToNavigate.emit(route)
        }
    }

    fun setCurrentScreen(screen: Screen?) {
        screenRepository.setCurrentScreen(screen)
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
                userConversationRepository.conversationsWithLastMessage
                    .map { conversationsMessage ->
                        conversationsMessage
                            .filter { it.lastMessage?.senderId != currentUser.id }
                            .filter { it.lastMessage?.isSeen() == false }
                            .mapNotNull { it.lastMessage }
                    }.collect { messages ->
                        _messageNavigationItem.value = NavigationItem.Message(badges = messages.size)
                    }
            }.launchIn(viewModelScope)
    }
}