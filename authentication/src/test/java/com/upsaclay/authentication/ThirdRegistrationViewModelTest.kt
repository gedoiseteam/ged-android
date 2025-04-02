package com.upsaclay.authentication

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.presentation.viewmodels.ThirdRegistrationViewModel
import com.upsaclay.common.domain.repository.UserRepository
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
class ThirdRegistrationViewModelTest {
    private val authenticationRepository: AuthenticationRepository = mockk()
    private val userRepository: UserRepository = mockk()

    private lateinit var thirdRegistrationViewModel: ThirdRegistrationViewModel
    
    private val testDispatcher = UnconfinedTestDispatcher()
    private val firstName = "John"
    private val lastName = "Doe"
    private val email = "email@example.com"
    private val password = "password1234"
    private val schoolLevel = "GED 2"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        thirdRegistrationViewModel = ThirdRegistrationViewModel(
            firstName = firstName,
            lastName = lastName,
            schoolLevel = schoolLevel,
            authenticationRepository = authenticationRepository,
            userRepository = userRepository
        )

        coEvery { authenticationRepository.registerWithEmailAndPassword(any(), any()) } returns Unit
        coEvery { userRepository.createUser(any()) } returns Unit
        coEvery { userRepository.isUserExist(any()) } returns false
    }

    @Test
    fun default_values_are_correct() {
        // Then
        assertEquals("", thirdRegistrationViewModel.email)
        assertEquals("", thirdRegistrationViewModel.password)
    }

    @Test
    fun updateEmail_should_update_email() {
        // When
        thirdRegistrationViewModel.updateEmail(email)

        // Then
        assertEquals(email, thirdRegistrationViewModel.email)
    }

    @Test
    fun updatePassword_should_update_password() {
        // When
        thirdRegistrationViewModel.updatePassword(password)

        // Then
        assertEquals(password, thirdRegistrationViewModel.password)
    }

    @Test
    fun register_should_register_user() = runTest {
        // Given
        thirdRegistrationViewModel.updateEmail(email)
        thirdRegistrationViewModel.updatePassword(password)

        // When
        thirdRegistrationViewModel.register()

        // Then
        coVerify { authenticationRepository.registerWithEmailAndPassword(email, password) }
    }

    @Test
    fun register_should_create_user() = runTest {
        // When
        thirdRegistrationViewModel.register()

        // Then
        coVerify { userRepository.createUser(any()) }
    }

    @Test
    fun validateCredentialInputs_should_return_true_when_email_and_password_are_not_empty() {
        // Given
        thirdRegistrationViewModel.updateEmail(email)
        thirdRegistrationViewModel.updatePassword(password)

        // When
        val result = thirdRegistrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(true, result)
    }

    @Test
    fun validateCredentialInputs_should_return_false_when_email_is_empty() {
        // Given
        thirdRegistrationViewModel.updateEmail("")
        thirdRegistrationViewModel.updatePassword(password)

        // When
        val result = thirdRegistrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun validateCredentialInputs_should_return_false_when_email_format_is_incorrect() {
        // Given
        thirdRegistrationViewModel.updateEmail("email")
        thirdRegistrationViewModel.updatePassword(password)

        // When
        val result = thirdRegistrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun validateCredentialInputs_should_return_false_when_password_is_empty() {
        // Given
        thirdRegistrationViewModel.updateEmail(email)
        thirdRegistrationViewModel.updatePassword("")

        // When
        val result = thirdRegistrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun validateCredentialInputs_should_return_false_when_password_length_is_shorter_than_8() {
        // Given
        thirdRegistrationViewModel.updateEmail(email)
        thirdRegistrationViewModel.updatePassword("pass")

        // When
        val result = thirdRegistrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(false, result)
    }
}