package com.upsaclay.gedoise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationScreenRoute
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.entity.ScreenRoute
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.gedoise.domain.entities.MainScreenRoute
import com.upsaclay.gedoise.domain.repository.ScreenRepository
import com.upsaclay.gedoise.presentation.NavigationItem
import com.upsaclay.message.domain.entity.MessageScreenRoute
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.news.domain.entity.NewsScreenRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class NavigationViewModel(
    userRepository: UserRepository,
    private val userConversationRepository: UserConversationRepository,
    private val screenRepository: ScreenRepository,
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {
    private val intentScreenNavigate = MutableStateFlow<ScreenRoute?>(null)

    private val _screenRouteToNavigate = MutableSharedFlow<ScreenRoute>(replay = 3)
    val screenRouteToNavigate: Flow<ScreenRoute> = _screenRouteToNavigate

    private val _startDestinationScreenRoute = MutableStateFlow<ScreenRoute>(MainScreenRoute.Splash)
    val startDestinationScreenRoute: StateFlow<ScreenRoute> = _startDestinationScreenRoute

    private val _homeNavigationItem = MutableStateFlow(NavigationItem.Home())
    val homeNavigationItem: Flow<NavigationItem> = _homeNavigationItem

    private val _messageNavigationItem = MutableStateFlow(NavigationItem.Message())
    val messageNavigationItem: Flow<NavigationItem> = _messageNavigationItem

    val currentUser: StateFlow<User?> = userRepository.currentUser

    init {
        updateStartDestinationScreenRoute()
        updateScreenRoutes()
        updateMessageNavigationItemBadges()
    }

    fun storeCurrentScreen(screenRoute: ScreenRoute?) {
        screenRepository.setCurrentScreenRoute(screenRoute)
    }

    fun updateIntentScreenNavigate(screenRoute: ScreenRoute) {
        intentScreenNavigate.value = screenRoute
    }

    private fun updateStartDestinationScreenRoute() {
        viewModelScope.launch {
            authenticationRepository.isAuthenticated
                .filterNotNull()
                .collect { isAuthenticated ->
                    _startDestinationScreenRoute.value = if (isAuthenticated) {
                        NewsScreenRoute.News
                    } else {
                        AuthenticationScreenRoute.Authentication
                    }
                }
        }
    }

    private fun updateScreenRoutes() {
        viewModelScope.launch {
            combine(
                authenticationRepository.isAuthenticated.filterNotNull(),
                intentScreenNavigate
            ) { isAuthenticated, intentScreen ->
                if (isAuthenticated) {
                    intentScreen
                }
                else {
                    if (screenRepository.currentScreenRoute !is AuthenticationScreenRoute) {
                        AuthenticationScreenRoute.Authentication
                    }
                    else null
                }
            }
                .filterNotNull()
                .collect {
                    navigate(it)
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun updateMessageNavigationItemBadges() {
        currentUser
            .filterNotNull()
            .distinctUntilChangedBy { it.id }
            .mapLatest { currentUser ->
                userConversationRepository.conversationsMessage
                    .map { conversationsMessage ->
                        conversationsMessage
                            .filterNot { it.lastMessage.isSeen() }
                            .filter { it.lastMessage.senderId != currentUser.id }
                    }.collect { messages ->
                        _messageNavigationItem.value = NavigationItem.Message(badges = messages.size)
                    }
            }.launchIn(viewModelScope)
    }

    private suspend fun navigate(screenRoute: ScreenRoute) {
        val routes: Array<ScreenRoute> = when(screenRoute) {
            is MessageScreenRoute.Chat -> {
                arrayOf(
                    MessageScreenRoute.Conversation,
                    MessageScreenRoute.Chat(screenRoute.conversation)
                )
            }
            is AuthenticationScreenRoute.Authentication -> {
                arrayOf(AuthenticationScreenRoute.Authentication)
            }
            else -> return
        }

        routes.forEach {
            _screenRouteToNavigate.emit(it)
        }
    }
}