package com.upsaclay.gedoise

import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.userFixture
import com.upsaclay.gedoise.data.ScreenRepository
import com.upsaclay.gedoise.domain.usecase.ClearDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import com.upsaclay.gedoise.presentation.viewmodels.NavigationViewModel
import com.upsaclay.message.domain.conversationsMessageFixture
import com.upsaclay.message.domain.repository.UserConversationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModelTest {
    private val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase = mockk()
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val startListeningDataUseCase: StartListeningDataUseCase = mockk()
    private val stopListeningDataUseCase: StopListeningDataUseCase = mockk()
    private val clearDataUseCase: ClearDataUseCase = mockk()
    private val userConversationRepository: UserConversationRepository = mockk()
    private val screenRepository: ScreenRepository = mockk()

    private lateinit var navigationViewModel: NavigationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { userConversationRepository.conversationsWithLastMessage } returns flowOf(conversationsMessageFixture)
        every { screenRepository.currentScreen } returns null
        every { screenRepository.setCurrentScreen(any()) } returns Unit
        every { getCurrentUserUseCase() } returns MutableStateFlow(userFixture)
        every { isUserAuthenticatedUseCase() } returns flowOf(true)
        coEvery { startListeningDataUseCase() } returns Unit
        coEvery { stopListeningDataUseCase() } returns Unit
        coEvery { clearDataUseCase() } returns Unit
    }

    @Test
    fun default_values_are_correct() = runTest {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf()

        // When
        navigationViewModel = NavigationViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            userConversationRepository = userConversationRepository,
            screenRepository = screenRepository
        )

        // Then
        assertEquals(userFixture, navigationViewModel.currentUser.value)
    }

    @Test
    fun authentication_state_should_be_AUTHENTICATED_when_user_is_authenticated() {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf(true)

        // When
        navigationViewModel = NavigationViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            userConversationRepository = userConversationRepository,
            screenRepository = screenRepository
        )

        // Then
        assertEquals(AuthenticationState.AUTHENTICATED, navigationViewModel.authenticationState.value)
    }

    @Test
    fun authentication_state_should_be_UNAUTHENTICATED_when_user_is_not_authenticated() {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf(false)

        // When
        navigationViewModel = NavigationViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            userConversationRepository = userConversationRepository,
            screenRepository = screenRepository
        )

        // Then
        assertEquals(AuthenticationState.UNAUTHENTICATED, navigationViewModel.authenticationState.value)
    }

    @Test
    fun data_listening_should_start_when_user_is_authenticated() {
        // When
        navigationViewModel = NavigationViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            userConversationRepository = userConversationRepository,
            screenRepository = screenRepository
        )

        // Then
        coVerify { startListeningDataUseCase() }
    }

    @Test
    fun data_listening_should_stop_when_user_is_not_authenticated() {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf(false)

        // When
        navigationViewModel = NavigationViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            userConversationRepository = userConversationRepository,
            screenRepository = screenRepository
        )

        // Then
        coVerify { stopListeningDataUseCase() }
    }

    @Test
    fun local_data_should_be_deleted_when_user_is_not_authenticated() = runTest {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf(false)

        // When
        navigationViewModel = NavigationViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            userConversationRepository = userConversationRepository,
            screenRepository = screenRepository
        )

        advanceUntilIdle()

        // Then
        coVerify { clearDataUseCase() }
    }
}