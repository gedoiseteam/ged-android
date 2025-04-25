package com.upsaclay.authentication

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.presentation.viewmodels.AuthenticationViewModel
import com.upsaclay.common.domain.repository.UserRepository
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
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationViewModelTest {
    private val authenticationRepository: AuthenticationRepository = mockk()
    private val userRepository: UserRepository = mockk()

    private lateinit var authenticationViewModel: AuthenticationViewModel
    private val email = "email@example.com"
    private val password = "password"
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        authenticationViewModel = AuthenticationViewModel(
            authenticationRepository = authenticationRepository,
            userRepository = userRepository
        )

        coEvery { authenticationRepository.loginWithEmailAndPassword(any(), any()) } returns Unit
        coEvery { authenticationRepository.setAuthenticated(any()) } returns Unit
        coEvery { userRepository.getUserWithEmail(any()) } returns userFixture
        coEvery { userRepository.setCurrentUser(any()) } returns Unit
    }

    @Test
    fun default_values_are_correct() {
        assertEquals("", authenticationViewModel.email.value)
        assertEquals("", authenticationViewModel.password.value)
    }

    @Test
    fun updateEmail_updates_email() {
        // When
        authenticationViewModel.updateEmail(email)

        // Then
        assertEquals(email, authenticationViewModel.email.value)
    }

    @Test
    fun updatePassword_updates_password() {
        // When
        authenticationViewModel.updatePassword(password)

        // Then
        assertEquals(password, authenticationViewModel.password.value)
    }

    @Test
    fun login_should_reset_password_when_exception_is_thrown() = runTest {
        // Given
        coEvery { authenticationRepository.loginWithEmailAndPassword(any(), any()) } throws Exception()
        authenticationViewModel.updatePassword(password)
        authenticationViewModel.updateEmail(email)

        // When
        authenticationViewModel.login()

        // Then
        assertEquals("", authenticationViewModel.password.value)
    }

    @Test
    fun login_should_set_current_user_when_login_is_successful() = runTest {
        // When
        authenticationViewModel.login()

        // Then
        coVerify { userRepository.setCurrentUser(userFixture) }
        coVerify { authenticationRepository.setAuthenticated(true) }
    }

    @Test
    fun verifyInputs_returns_false_when_email_is_blank() {
        // Given
        authenticationViewModel.updatePassword(email)

        // When
        val result = authenticationViewModel.verifyInputs()

        // Then
        assertFalse(result)
    }

    @Test
    fun verifyInputs_returns_false_when_password_is_blank() {
        // Given
        authenticationViewModel.updateEmail(email)

        // When
        val result = authenticationViewModel.verifyInputs()

        // Then
        assertFalse(result)
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