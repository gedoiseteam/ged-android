package com.upsaclay.gedoise

import com.upsaclay.authentication.domain.entity.AuthenticationEvent
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.userFixture
import com.upsaclay.gedoise.domain.usecase.ClearDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import com.upsaclay.gedoise.presentation.viewmodels.MainViewModel
import com.upsaclay.message.domain.messagesFixture
import com.upsaclay.message.domain.usecase.GetAllLastUnreadMessagesReceivedUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.replay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase = mockk()
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val startListeningDataUseCase: StartListeningDataUseCase = mockk()
    private val stopListeningDataUseCase: StopListeningDataUseCase = mockk()
    private val clearDataUseCase: ClearDataUseCase = mockk()
    private val getAllLastUnreadMessagesReceivedUseCase: GetAllLastUnreadMessagesReceivedUseCase = mockk()

    private lateinit var mainViewModel: MainViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getCurrentUserUseCase() } returns MutableStateFlow(userFixture)
        every { isUserAuthenticatedUseCase() } returns flowOf(true)
        every { getAllLastUnreadMessagesReceivedUseCase(any()) } returns flowOf(messagesFixture)
        coEvery { startListeningDataUseCase() } returns Unit
        coEvery { stopListeningDataUseCase() } returns Unit
        coEvery { clearDataUseCase() } returns Unit
    }

    @Test
    fun default_values_are_correct() = runTest {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf()

        // When
        mainViewModel = MainViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            getAllLastUnreadMessagesReceivedUseCase = getAllLastUnreadMessagesReceivedUseCase
        )

        // Then
        assertEquals(userFixture, mainViewModel.currentUser.value)
    }

    @Test
    fun authentication_state_should_be_AUTHENTICATED_when_user_is_authenticated() {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf(true)

        // When
        mainViewModel = MainViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            getAllLastUnreadMessagesReceivedUseCase = getAllLastUnreadMessagesReceivedUseCase
        )

        // Then
        assertEquals(AuthenticationEvent.Authenticated, mainViewModel.event.replayCache.first())
    }

    @Test
    fun authentication_state_should_be_UNAUTHENTICATED_when_user_is_not_authenticated() {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf(false)

        // When
        mainViewModel = MainViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            getAllLastUnreadMessagesReceivedUseCase = getAllLastUnreadMessagesReceivedUseCase
        )

        // Then
        assertEquals(AuthenticationEvent.Authenticated, mainViewModel.event.replayCache.first())
    }

    @Test
    fun data_listening_should_start_when_user_is_authenticated() {
        // When
        mainViewModel = MainViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            getAllLastUnreadMessagesReceivedUseCase = getAllLastUnreadMessagesReceivedUseCase
        )

        // Then
        coVerify { startListeningDataUseCase() }
    }

    @Test
    fun data_listening_should_stop_when_user_is_not_authenticated() {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf(false)

        // When
        mainViewModel = MainViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            getAllLastUnreadMessagesReceivedUseCase = getAllLastUnreadMessagesReceivedUseCase
        )

        // Then
        coVerify { stopListeningDataUseCase() }
    }

    @Test
    fun local_data_should_be_deleted_when_user_is_not_authenticated() = runTest {
        // Given
        every { isUserAuthenticatedUseCase() } returns flowOf(false)

        // When
        mainViewModel = MainViewModel(
            isUserAuthenticatedUseCase = isUserAuthenticatedUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startListeningDataUseCase = startListeningDataUseCase,
            stopListeningDataUseCase = stopListeningDataUseCase,
            clearDataUseCase = clearDataUseCase,
            getAllLastUnreadMessagesReceivedUseCase = getAllLastUnreadMessagesReceivedUseCase
        )

        advanceUntilIdle()

        // Then
        coVerify { clearDataUseCase() }
    }
}