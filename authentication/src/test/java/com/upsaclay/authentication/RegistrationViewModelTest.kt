package com.upsaclay.authentication

import com.upsaclay.authentication.domain.entity.RegistrationScreenState
import com.upsaclay.authentication.domain.entity.exception.AuthenticationException
import com.upsaclay.authentication.domain.usecase.RegisterUseCase
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import com.upsaclay.common.domain.usecase.IsUserExistUseCase
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
    private val createUserUseCase: CreateUserUseCase = mockk()
    private val registerUseCase: RegisterUseCase = mockk()
    private val isUserExistUseCase: IsUserExistUseCase = mockk()

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
            createUserUseCase = createUserUseCase,
            registerUseCase = registerUseCase,
            isUserExistUseCase = isUserExistUseCase
        )

        coEvery { registerUseCase(any(), any()) } returns userFixture.id
        coEvery { createUserUseCase(any()) } returns Unit
        coEvery { isUserExistUseCase(any()) } returns false
    }

    @Test
    fun default_values_are_correct() {
        // Then
        assertEquals(RegistrationScreenState.NOT_REGISTERED, registrationViewModel.screenState.value)
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
    fun reset_screen_state_should_reset_screen_state() {
        // When
        registrationViewModel.resetScreenState()

        // Then
        assertEquals(RegistrationScreenState.NOT_REGISTERED, registrationViewModel.screenState.value)
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
        coVerify { registerUseCase(email, password) }
        assertEquals(RegistrationScreenState.REGISTERED, registrationViewModel.screenState.value)
    }

    @Test
    fun register_should_create_user() = runTest {
        // Given
        val user = userFixture.copy(isMember = false, profilePictureUrl = null)
        registrationViewModel.updateFirstName(user.firstName)
        registrationViewModel.updateLastName(user.lastName)
        registrationViewModel.updateEmail(user.email)
        registrationViewModel.updatePassword(password)
        registrationViewModel.updateSchoolLevel(user.schoolLevel)

        // When
        registrationViewModel.register()

        // Then
        coVerify { createUserUseCase(user) }
    }


    @Test
    fun register_should_update_screen_state_to_USER_ALREADY_EXISTS_when_user_already_exists() = runTest {
        // Given
        coEvery { isUserExistUseCase(any()) } returns true

        // When
        registrationViewModel.register()

        // Then
        assertEquals(RegistrationScreenState.USER_ALREADY_EXISTS, registrationViewModel.screenState.value)
    }

    @Test
    fun register_should_update_screen_state_to_USER_ALREADY_EXISTS_when_email_is_already_affiliated() = runTest {
        // Given
        coEvery { registerUseCase(any(), any()) } throws AuthenticationException()

        // When
        registrationViewModel.register()

        // Then
        assertEquals(RegistrationScreenState.USER_ALREADY_EXISTS, registrationViewModel.screenState.value)
    }

    @Test
    fun register_should_update_screen_state_to_ERROR_when_unknown_error_throwing() = runTest {
        // Given
        coEvery { registerUseCase(any(), any()) } throws Exception()

        // When
        registrationViewModel.register()

        // Then
        assertEquals(RegistrationScreenState.UNKNOWN_ERROR, registrationViewModel.screenState.value)
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
        assertEquals(RegistrationScreenState.EMPTY_FIELDS_ERROR, registrationViewModel.screenState.value)
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
        assertEquals(RegistrationScreenState.EMAIL_FORMAT_ERROR, registrationViewModel.screenState.value)
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
        assertEquals(RegistrationScreenState.PASSWORD_LENGTH_ERROR, registrationViewModel.screenState.value)
        assertEquals(false, result)
    }
}