package com.upsaclay.authentication

import com.upsaclay.authentication.domain.entity.RegistrationErrorType
import com.upsaclay.authentication.domain.entity.RegistrationEvent
import com.upsaclay.authentication.domain.entity.exception.InvalidCredentialsException
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.ErrorType
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

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {
    private val authenticationRepository: AuthenticationRepository = mockk()
    private val userRepository: UserRepository = mockk()

    private lateinit var registrationViewModel: RegistrationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val firstName = "John"
    private val lastName = "Doe"
    private val email = "email@example.com"
    private val password = "password1234"
    private val schoolLevel = "GED 2"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        registrationViewModel = RegistrationViewModel(
            authenticationRepository = authenticationRepository,
            userRepository = userRepository
        )

        coEvery { authenticationRepository.registerWithEmailAndPassword(any(), any()) } returns Unit
        coEvery { userRepository.createUser(any()) } returns Unit
        coEvery {userRepository.isUserExist(any()) } returns false
    }

    @Test
    fun default_values_are_correct() {
        // Then
        assertEquals("", registrationViewModel.firstName)
        assertEquals("", registrationViewModel.lastName)
        assertEquals("", registrationViewModel.email)
        assertEquals("", registrationViewModel.password)
        assertEquals(listOf("GED 1", "GED 2", "GED 3", "GED 4"), registrationViewModel.schoolLevels)
        assertEquals("GED 1", registrationViewModel.schoolLevel)
    }

    @Test
    fun updateFirstName_should_update_firstName() {
        // When
        registrationViewModel.updateFirstName(firstName)

        // Then
        assertEquals(firstName, registrationViewModel.firstName)
    }

    @Test
    fun updateLastName_should_update_lastName() {
        // When
        registrationViewModel.updateLastName(lastName)

        // Then
        assertEquals(lastName, registrationViewModel.lastName)
    }

    @Test
    fun updateEmail_should_update_email() {
        // When
        registrationViewModel.updateEmail(email)

        // Then
        assertEquals(email, registrationViewModel.email)
    }

    @Test
    fun updatePassword_should_update_password() {
        // When
        registrationViewModel.updatePassword(password)

        // Then
        assertEquals(password, registrationViewModel.password)
    }

    @Test
    fun updateSchoolLevel_should_update_schoolLevel() {
        // When
        registrationViewModel.updateSchoolLevel(schoolLevel)

        // Then
        assertEquals(schoolLevel, registrationViewModel.schoolLevel)
    }

    @Test
    fun reset_first_name_should_reset_first_name() {
        // Given
        registrationViewModel.updateFirstName(firstName)

        // When
        registrationViewModel.resetFirstName()

        // Then
        assertEquals("", registrationViewModel.firstName)
    }

    @Test
    fun reset_last_name_should_reset_last_name() {
        // Given
        registrationViewModel.updateLastName(lastName)

        // When
        registrationViewModel.resetLastName()

        // Then
        assertEquals("", registrationViewModel.lastName)
    }

    @Test
    fun reset_email_should_reset_email() {
        // Given
        registrationViewModel.updateEmail(email)

        // When
        registrationViewModel.resetEmail()

        // Then
        assertEquals("", registrationViewModel.email)
    }

    @Test
    fun reset_password_should_reset_password() {
        // Given
        registrationViewModel.updatePassword(password)

        // When
        registrationViewModel.resetPassword()

        // Then
        assertEquals("", registrationViewModel.password)
    }

    @Test
    fun reset_school_level_should_reset_school_level() {
        // Given
        registrationViewModel.updateSchoolLevel(schoolLevel)

        // When
        registrationViewModel.resetSchoolLevel()

        // Then
        assertEquals("GED 1", registrationViewModel.schoolLevel)
    }

    @Test
    fun register_should_register_user() = runTest {
        // Given
        registrationViewModel.updateFirstName(firstName)
        registrationViewModel.updateLastName(lastName)
        registrationViewModel.updateEmail(email)
        registrationViewModel.updatePassword(password)
        registrationViewModel.updateSchoolLevel(schoolLevel)

        // When
        registrationViewModel.register()

        // Then
        coVerify { authenticationRepository.registerWithEmailAndPassword(email, password) }
    }

    @Test
    fun register_should_create_user() = runTest {
        // When
        registrationViewModel.register()

        // Then
        coVerify { userRepository.createUser(any()) }
    }

    @Test
    fun verifyNamesInputs_should_return_true_when_names_are_not_empty() {
        // Given
        registrationViewModel.updateFirstName(firstName)
        registrationViewModel.updateLastName(lastName)

        // When
        val result = registrationViewModel.verifyNamesInputs()

        // Then
        assertEquals(true, result)
    }

    @Test
    fun verifyNamesInputs_should_return_false_when_names_are_empty() {
        // When
        val result = registrationViewModel.verifyNamesInputs()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun verifyNamesInputs_should_trim_and_uppercase_names() {
        // Given
        registrationViewModel.updateFirstName("john ")
        registrationViewModel.updateLastName(" doe")

        // When
        registrationViewModel.verifyNamesInputs()

        // Then
        assertEquals(firstName, registrationViewModel.firstName)
        assertEquals(lastName, registrationViewModel.lastName)
    }

    @Test
    fun validateCredentialInputs_should_return_true_when_email_and_password_are_not_empty() {
        // Given
        registrationViewModel.updateEmail(email)
        registrationViewModel.updatePassword(password)

        // When
        val result = registrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(true, result)
    }

    @Test
    fun validateCredentialInputs_should_return_false_when_email_is_empty() {
        // Given
        registrationViewModel.updateEmail("")
        registrationViewModel.updatePassword(password)

        // When
        val result = registrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun validateCredentialInputs_should_return_false_when_email_format_is_incorrect() {
        // Given
        registrationViewModel.updateEmail("email")
        registrationViewModel.updatePassword(password)

        // When
        val result = registrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun validateCredentialInputs_should_return_false_when_password_is_empty() {
        // Given
        registrationViewModel.updateEmail(email)
        registrationViewModel.updatePassword("")

        // When
        val result = registrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun validateCredentialInputs_should_return_false_when_password_length_is_shorter_than_8() {
        // Given
        registrationViewModel.updateEmail(email)
        registrationViewModel.updatePassword("pass")

        // When
        val result = registrationViewModel.validateCredentialInputs()

        // Then
        assertEquals(false, result)
    }
}