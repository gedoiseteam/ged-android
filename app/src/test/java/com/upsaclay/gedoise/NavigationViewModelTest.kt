package com.upsaclay.gedoise

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.userFixture
import com.upsaclay.common.domain.usersFixture
import com.upsaclay.gedoise.domain.repository.ScreenRepository
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
        every { screenRepository.currentScreenRoute } returns null
        every { screenRepository.setCurrentScreenRoute(any()) } returns Unit
        every { userRepository.currentUser } returns MutableStateFlow(userFixture)
        every { authenticationRepository.isAuthenticated } returns flowOf(true)
        coEvery { userRepository.getCurrentUser() } returns userFixture
        coEvery { userRepository.getUsers() } returns usersFixture
        coEvery { userRepository.getUser(any()) } returns userFixture
        coEvery { userRepository.setCurrentUser(any()) } returns Unit
    }

    @Test
    fun default_values_are_correct() = runTest {
        // Given
        every { authenticationRepository.isAuthenticated } returns flowOf()

        // When
        navigationViewModel = NavigationViewModel(
            userRepository = userRepository,
            userConversationRepository = userConversationRepository,
            screenRepository = screenRepository,
            authenticationRepository = authenticationRepository
        )

        // Then
        assertEquals(userFixture, navigationViewModel.currentUser.value)
    }
}