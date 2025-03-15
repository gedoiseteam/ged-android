package com.upsaclay.authentication

import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.SendVerificationEmailUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.authentication.presentation.viewmodels.EmailVerificationViewModel
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
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase = mockk()
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase = mockk()
    private val setUserAuthenticatedUseCase: SetUserAuthenticatedUseCase = mockk()

    private lateinit var emailVerificationViewModel: EmailVerificationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        emailVerificationViewModel = EmailVerificationViewModel(
            email = userFixture.email,
            sendVerificationEmailUseCase = sendVerificationEmailUseCase,
            isEmailVerifiedUseCase = isEmailVerifiedUseCase,
            setUserAuthenticatedUseCase = setUserAuthenticatedUseCase
        )

        coEvery { isEmailVerifiedUseCase() } returns true
        coEvery { sendVerificationEmailUseCase() } returns Unit
        coEvery { setUserAuthenticatedUseCase(any()) } returns Unit
    }

    @Test
    fun default_values_are_correct() {
        // Then
        assertEquals(AuthenticationScreenState.DEFAULT, emailVerificationViewModel.screenState.value)
    }

    @Test
    fun sendVerificationEmail_should_send_verification_email() {
        // When
        emailVerificationViewModel.sendVerificationEmail()

        // Then
        coVerify { sendVerificationEmailUseCase() }
    }

    @Test
    fun sendVerificationEmail_should_update_screen_state_to_TOO_MANY_REQUESTS_ERROR_when_too_many_request_are_sent() = runTest {
        // Given
        coEvery { sendVerificationEmailUseCase() } throws TooManyRequestException()

        // When
        emailVerificationViewModel.sendVerificationEmail()

        // Then
        assertEquals(AuthenticationScreenState.TOO_MANY_REQUESTS_ERROR, emailVerificationViewModel.screenState.value)
    }

    @Test
    fun sendVerificationEmail_should_update_screen_state_to_UNKNOWN_ERROR_when_an_unknown_error_occurs() = runTest {
        // Given
        coEvery { sendVerificationEmailUseCase() } throws Exception()

        // When
        emailVerificationViewModel.sendVerificationEmail()

        // Then
        assertEquals(AuthenticationScreenState.UNKNOWN_ERROR, emailVerificationViewModel.screenState.value)
    }

    @Test
    fun verifyIsEmailVerified_should_update_screen_state_to_EMAIL_VERIFIED_and_set_user_authenticated_to_true_when_email_is_verified() = runTest {
        // When
        emailVerificationViewModel.verifyIsEmailVerified()

        // Then
        assertEquals(AuthenticationScreenState.EMAIL_VERIFIED, emailVerificationViewModel.screenState.value)
        coVerify { setUserAuthenticatedUseCase(true) }
    }

    @Test
    fun verifyIsEmailVerified_should_update_screen_state_to_EMAIL_NOT_VERIFIED_when_email_is_not_verified() = runTest {
        // Given
        coEvery { isEmailVerifiedUseCase() } returns false

        // When
        emailVerificationViewModel.verifyIsEmailVerified()

        // Then
        assertEquals(AuthenticationScreenState.EMAIL_NOT_VERIFIED, emailVerificationViewModel.screenState.value)
    }

    @Test
    fun verifyIsEmailVerified_should_update_screen_state_to_UNKNOWN_ERROR_when_an_unknown_error_occurs() = runTest {
        // Given
        coEvery { isEmailVerifiedUseCase() } throws Exception()

        // When
        emailVerificationViewModel.verifyIsEmailVerified()

        // Then
        assertEquals(AuthenticationScreenState.UNKNOWN_ERROR, emailVerificationViewModel.screenState.value)
    }
}