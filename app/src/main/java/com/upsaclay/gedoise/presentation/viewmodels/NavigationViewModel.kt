package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.entity.ScreenRoute
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.gedoise.data.ScreenRepository
import com.upsaclay.gedoise.domain.usecase.ClearDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import com.upsaclay.gedoise.presentation.NavigationItem
import com.upsaclay.message.domain.entity.MessageScreenRoute
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
    private val startListeningDataUseCase: StartListeningDataUseCase,
    private val stopListeningDataUseCase: StopListeningDataUseCase,
    private val clearDataUseCase: ClearDataUseCase,
    userRepository: UserRepository,
    private val userConversationRepository: UserConversationRepository,
    private val screenRepository: ScreenRepository,
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {
    private val _authenticationState = MutableStateFlow(AuthenticationState.WAITING)
    val authenticationState: StateFlow<AuthenticationState> = _authenticationState

    private val _routeToNavigate = MutableSharedFlow<String>()
    val routeToNavigate: Flow<String> = _routeToNavigate

    private val _homeNavigationItem = MutableStateFlow(NavigationItem.Home())
    val homeNavigationItem: Flow<NavigationItem> = _homeNavigationItem

    private val _messageNavigationItem = MutableStateFlow(NavigationItem.Message())
    val messageNavigationItem: Flow<NavigationItem> = _messageNavigationItem

    val currentUser: StateFlow<User?> = userRepository.currentUser

    init {
        listenAuthenticationState()
        updateMessageNavigationItemBadges()
    }

    fun navigateTo(screenRoute: ScreenRoute) {
        val route = when(screenRoute) {
            is MessageScreenRoute.Chat -> MessageScreenRoute.Chat(screenRoute.conversation).route
            else -> return
        }
        viewModelScope.launch {
            _routeToNavigate.emit(route)
        }
    }

    fun setCurrentScreen(screenRoute: ScreenRoute?) {
        screenRepository.setCurrentScreen(screenRoute)
    }

    private fun listenAuthenticationState() {
        authenticationRepository.isAuthenticated
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