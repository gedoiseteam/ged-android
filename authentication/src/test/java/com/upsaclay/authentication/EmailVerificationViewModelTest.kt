package com.upsaclay.authentication

import com.upsaclay.authentication.domain.entity.AuthenticationEvent
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.presentation.viewmodels.EmailVerificationViewModel
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.domain.entity.TooManyRequestException
import com.upsaclay.common.domain.userFixture
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class EmailVerificationViewModelTest {
    private val authenticationRepository: AuthenticationRepository = mockk()

    private lateinit var emailVerificationViewModel: EmailVerificationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        emailVerificationViewModel = EmailVerificationViewModel(
            email = userFixture.email,
            authenticationRepository = authenticationRepository
        )

        coEvery { authenticationRepository.isUserEmailVerified() } returns true
        coEvery { authenticationRepository.sendVerificationEmail() } returns Unit
    }

    @Test
    fun sendVerificationEmail_should_send_verification_email() {
        // When
        emailVerificationViewModel.sendVerificationEmail()

        // Then
        coVerify { authenticationRepository.sendVerificationEmail() }
    }

    @Test
    fun verifyIsEmailVerified_should_set_user_authenticated_to_true_when_email_is_verified() = runTest {
        // When
        emailVerificationViewModel.verifyIsEmailVerified()

        // Then
        coVerify { authenticationRepository.setAuthenticated(true) }
    }
}