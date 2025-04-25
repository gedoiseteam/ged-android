package com.upsaclay.gedoise

import com.upsaclay.authentication.domain.entity.AuthenticationScreenRoute
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.userFixture
import com.upsaclay.common.domain.usersFixture
import com.upsaclay.gedoise.domain.repository.ScreenRepository
import com.upsaclay.gedoise.presentation.viewmodels.NavigationViewModel
import com.upsaclay.message.domain.conversationFixture
import com.upsaclay.message.domain.conversationMessageFixture
import com.upsaclay.message.domain.conversationsMessageFixture
import com.upsaclay.message.domain.entity.MessageScreenRoute
import com.upsaclay.message.domain.messageFixture2
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.news.domain.entity.NewsScreenRoute
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModelTest {
    private val userRepository: UserRepository = mockk()
    private val userConversationRepository: UserConversationRepository = mockk()
    private val screenRepository: ScreenRepository = mockk()
    private val authenticationRepository: AuthenticationRepository = mockk()

    private lateinit var navigationViewModel: NavigationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { userConversationRepository.conversationsMessage } returns flowOf(conversationsMessageFixture)
        every { authenticationRepository.isAuthenticated } returns flowOf(true)
        every { screenRepository.currentScreenRoute } returns null
        every { userRepository.currentUser } returns MutableStateFlow(userFixture)
        every { screenRepository.setCurrentScreenRoute(any()) } returns Unit
        coEvery { userRepository.getCurrentUser() } returns userFixture
        coEvery { userRepository.getUsers() } returns usersFixture
        coEvery { userRepository.getUser(any()) } returns userFixture
        coEvery { userRepository.setCurrentUser(any()) } returns Unit

        navigationViewModel = NavigationViewModel(
            userRepository = userRepository,
            userConversationRepository = userConversationRepository,
            screenRepository = screenRepository,
            authenticationRepository = authenticationRepository
        )
    }

    @Test
    fun default_values_are_correct() = runTest {
        // Given
        every { authenticationRepository.isAuthenticated } returns flowOf()

        // When
        navigationViewModel.start()

        // Then
        assertEquals(userFixture, navigationViewModel.currentUser.value)
    }

    @Test
    fun startDestinationScreenRoute_should_be_news_screen_when_authenticated() = runTest {
        // When
        navigationViewModel.start()

        // Then
        val result = navigationViewModel.startDestinationScreenRoute.value

        assertEquals(NewsScreenRoute.News, result)
    }

    @Test
    fun startDestinationScreenRoute_should_be_authentication_screen_when_unauthenticated() = runTest {
        // Given
        every { authenticationRepository.isAuthenticated } returns flowOf(false)

        // When
        navigationViewModel.start()

        // Then
        val result = navigationViewModel.startDestinationScreenRoute.value

        assertEquals(AuthenticationScreenRoute.Authentication, result)
    }

    @Test
    fun should_navigate_to_intent_screen_when_authenticated() = runTest {
        // Given
        val screen = MessageScreenRoute.Chat(conversationFixture)

        // When
        navigationViewModel.start()
        navigationViewModel.intentToNavigate(screen)

        // Then
        val result = navigationViewModel.screenRouteToNavigate.replayCache.last()

        assertEquals(screen, result)
    }

    @Test
    fun should_navigate_to_authentication_screen_when_unauthenticated() = runTest {
        // Given
        every { authenticationRepository.isAuthenticated } returns flowOf(false)
        val screen = MessageScreenRoute.Chat(conversationFixture)

        // When
        navigationViewModel.start()
        navigationViewModel.intentToNavigate(screen)

        // Then
        val result = navigationViewModel.screenRouteToNavigate.replayCache.last()

        assertEquals(AuthenticationScreenRoute.Authentication, result)
    }

    @Test
    fun should_not_navigate_when_current_screen_is_one_of_authentication_screen_when_unauthenticated() = runTest {
        // Given
        val screen = MessageScreenRoute.Chat(conversationFixture)
        every { authenticationRepository.isAuthenticated } returns flowOf(false)
        every { screenRepository.currentScreenRoute } returns AuthenticationScreenRoute.FirstRegistration

        // When
        navigationViewModel.start()
        navigationViewModel.intentToNavigate(screen)

        // Then
        val result = navigationViewModel.screenRouteToNavigate.replayCache.lastOrNull()

        assertNull(result)
    }

    @Test
    fun messageNavigationBadges_should_be_equals_to_conversationsMessage_with_unread_message() {
        // Given
        val unreadMessage = 2
        val conversationsMessage = listOf(
            conversationMessageFixture.copy(lastMessage = messageFixture2),
            conversationMessageFixture.copy(
                conversation = conversationFixture.copy(id = 2),
                lastMessage = messageFixture2
            )
        )
        every { userConversationRepository.conversationsMessage } returns flowOf(conversationsMessage)

        // When
        navigationViewModel.start()

        // Then
        val result = navigationViewModel.messageNavigationItem.value

        assertEquals(unreadMessage, result.badges)
    }
}