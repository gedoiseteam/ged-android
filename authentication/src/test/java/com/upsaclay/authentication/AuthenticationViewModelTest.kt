package com.upsaclay.authentication

import android.accounts.NetworkErrorException
import com.upsaclay.authentication.domain.entity.AuthErrorType
import com.upsaclay.authentication.domain.entity.AuthenticationEvent
import com.upsaclay.authentication.domain.entity.exception.InvalidCredentialsException
import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.LoginUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.authentication.presentation.viewmodels.AuthenticationViewModel
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.domain.entity.TooManyRequestException
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import com.upsaclay.common.domain.usecase.GetUserUseCase
import com.upsaclay.common.domain.usecase.SetCurrentUserUseCase
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
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationViewModelTest {
    private val loginUseCase: LoginUseCase = mockk()
    private val setUserAuthenticatedUseCase: SetUserAuthenticatedUseCase = mockk()
    private val getUserUseCase: GetUserUseCase = mockk()
    private val setCurrentUserUseCase: SetCurrentUserUseCase = mockk()
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase = mockk()
    private val createUserUseCase: CreateUserUseCase = mockk()

    private lateinit var authenticationViewModel: AuthenticationViewModel
    private val email = "email@example.com"
    private val password = "password"
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        authenticationViewModel = AuthenticationViewModel(
            loginUseCase = loginUseCase,
            setUserAuthenticatedUseCase = setUserAuthenticatedUseCase,
            getUserUseCase = getUserUseCase,
            setCurrentUserUseCase = setCurrentUserUseCase,
            isEmailVerifiedUseCase = isEmailVerifiedUseCase
        )

        coEvery { isEmailVerifiedUseCase() } returns true
        coEvery { loginUseCase(any(), any()) } returns Unit
        coEvery { setUserAuthenticatedUseCase(any()) } returns Unit
        coEvery { getUserUseCase.withEmail(any()) } returns userFixture
        coEvery { setCurrentUserUseCase(any()) } returns Unit
    }

    @Test
    fun default_values_are_correct() {
        assertEquals("", authenticationViewModel.email)
        assertEquals("", authenticationViewModel.password)
    }

    @Test
    fun updateEmail_updates_email() {
        // When
        authenticationViewModel.updateEmail(email)

        // Then
        assertEquals(email, authenticationViewModel.email)
    }

    @Test
    fun updatePassword_updates_password() {
        // When
        authenticationViewModel.updatePassword(password)

        // Then
        assertEquals(password, authenticationViewModel.password)
    }

    @Test
    fun login_sets_screen_state_to_EMAIL_NOT_VERIFIED_when_email_is_not_verified() = runTest {
        // Given
        coEvery { isEmailVerifiedUseCase() } returns false

        // When
        authenticationViewModel.login()

        // Then
        assertEquals(AuthenticationEvent.EmailNotVerified, authenticationViewModel.event.replayCache[0])
    }

    @Test
    fun login_sets_screen_state_to_AUTHENTICATED_USER_NOT_FOUND_when_user_is_not_found() = runTest {
        // Given
        coEvery { getUserUseCase.withEmail(any()) } returns null

        // When
        authenticationViewModel.login()

        // Then
        val result = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(AuthErrorType.AUTH_USER_NOT_FOUND, result.type)
    }

    @Test
    fun login_sets_screen_state_to_SERVER_COMMUNICATION_ERROR_when_a_io_exception_is_thrown() = runTest {
        // Given
        coEvery { loginUseCase(any(), any()) } throws IOException()

        // When
        authenticationViewModel.login()

        // Then
        val result = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(ErrorType.InternalServerError, result.type)
    }

    @Test
    fun login_sets_screen_state_to_TOO_MANY_REQUESTS_ERROR_when_a_too_many_request_exception_is_thrown() = runTest {
        // Given
        coEvery { loginUseCase(any(), any()) } throws TooManyRequestException()

        // When
        authenticationViewModel.login()

        // Then
        val result = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(ErrorType.TooManyRequestsError, result.type)
    }

    @Test
    fun login_sets_screen_state_to_AUTHENTICATION_ERROR_when_an_authentication_exception_is_thrown_with_invalid_credentials_code() = runTest {
        // Given
        coEvery { loginUseCase(any(), any()) } throws InvalidCredentialsException()

        // When
        authenticationViewModel.login()

        // Then
        val result = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(AuthErrorType.INVALID_CREDENTIALS_ERROR, result.type)
    }

    @Test
    fun login_sets_screen_state_to_SERVER_COMMUNICATION_ERROR_when_an_io_exception_is_thrown() = runTest {
        // Given
        coEvery { loginUseCase(any(), any()) } throws IOException()

        // When
        authenticationViewModel.login()

        // Then
        val result = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(ErrorType.InternalServerError, result.type)
    }

    @Test
    fun login_sets_screen_state_to_NETWORK_ERROR_when_an_network_error_exception_is_thrown() = runTest {
        // Given
        coEvery { loginUseCase(any(), any()) } throws NetworkErrorException()

        // When
        authenticationViewModel.login()

        // Then
        val result = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(ErrorType.NetworkError, result.type)
    }

    @Test
    fun login_sets_screen_state_to_UNKNOWN_ERROR_when_an_unknown_exception_is_thrown() = runTest {
        // Given
        coEvery { loginUseCase(any(), any()) } throws Exception()

        // When
        authenticationViewModel.login()

        // Then
        val result = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(ErrorType.UnknownError, result.type)
    }

    @Test
    fun login_sets_screen_state_to_TOO_MANY_REQUESTS_ERROR_when_an_too_many_request_exception_is_thrown() = runTest {
        // Given
        coEvery { loginUseCase(any(), any()) } throws TooManyRequestException()

        // When
        authenticationViewModel.login()

        // Then
        val result = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(ErrorType.TooManyRequestsError, result.type)
    }

    @Test
    fun login_sets_screen_state_to_AUTHENTICATION_ERRORERROR_when_an_authentication_exception_is_thrown() = runTest {
        // Given
        coEvery { loginUseCase(any(), any()) } throws InvalidCredentialsException()

        // When
        authenticationViewModel.login()

        // Then
        val result = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(AuthErrorType.INVALID_CREDENTIALS_ERROR, result.type)
    }

    @Test
    fun login_should_reset_password_when_exception_is_thrown() = runTest {
        // Given
        coEvery { loginUseCase(any(), any()) } throws Exception()
        authenticationViewModel.updatePassword(password)
        authenticationViewModel.updateEmail(email)

        // When
        authenticationViewModel.login()

        // Then
        assertEquals("", authenticationViewModel.password)
    }

    @Test
    fun login_should_set_current_user_when_login_is_successful() = runTest {
        // When
        authenticationViewModel.login()

        // Then
        coVerify { setCurrentUserUseCase(userFixture) }
        coVerify { setUserAuthenticatedUseCase(true) }
    }

    @Test
    fun verifyInputs_returns_false_when_email_is_blank() {
        // Given
        authenticationViewModel.updatePassword(email)

        // When
        val result = authenticationViewModel.verifyInputs()

        // Then
        assertFalse(result)
        val event = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(AuthErrorType.EMPTY_FIELDS_ERROR, event.type)
    }

    @Test
    fun verifyInputs_returns_false_when_password_is_blank() {
        // Given
        authenticationViewModel.updateEmail(email)

        // When
        val result = authenticationViewModel.verifyInputs()

        // Then
        assertFalse(result)
        val event = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(AuthErrorType.EMPTY_FIELDS_ERROR, event.type)
    }

    @Test
    fun verifyInputs_returns_false_when_email_have_an_invalid_format() {
        // Given
        authenticationViewModel.updateEmail("email")
        authenticationViewModel.updatePassword(password)

        // When
        val result = authenticationViewModel.verifyInputs()

        // Then
        assertFalse(result)
        val event = authenticationViewModel.event.replayCache[0] as AuthenticationEvent.Error
        assertEquals(AuthErrorType.EMAIL_FORMAT_ERROR, event.type)
    }

    @Test
    fun verifyInputs_returns_true_when_email_and_password_are_not_blank_and_they_have_correct_format() {
        // Given
        authenticationViewModel.updateEmail(email)
        authenticationViewModel.updatePassword(password)

        // When
        val result = authenticationViewModel.verifyInputs()

        // Then
        assert(result)
    }
}