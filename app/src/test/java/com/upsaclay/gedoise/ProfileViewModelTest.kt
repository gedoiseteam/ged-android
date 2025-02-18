package com.upsaclay.gedoise

import com.upsaclay.authentication.domain.usecase.LogoutUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.userFixture
import com.upsaclay.gedoise.presentation.viewmodels.ProfileViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val logoutUseCase: LogoutUseCase = mockk()

    private lateinit var profileViewModel: ProfileViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getCurrentUserUseCase() } returns MutableStateFlow(userFixture)
        coEvery { logoutUseCase() } returns Unit

        profileViewModel = ProfileViewModel(
            getCurrentUserUseCase = getCurrentUserUseCase,
            logoutUseCase = logoutUseCase
        )
    }

    @Test
    fun default_values_are_correct() {
        // Given
        every { getCurrentUserUseCase() } returns MutableStateFlow(null)

        // When
        profileViewModel = ProfileViewModel(
            getCurrentUserUseCase = getCurrentUserUseCase,
            logoutUseCase = logoutUseCase
        )

        // Then
        assertEquals(null, profileViewModel.currentUser.value)
    }

    @Test
    fun logout_should_logout_user() {
        // When
        profileViewModel.logout()

        // Then
        coVerify { logoutUseCase() }
    }
}