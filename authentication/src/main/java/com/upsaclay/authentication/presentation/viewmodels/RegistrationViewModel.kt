package com.upsaclay.authentication.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.RegistrationScreenState
import com.upsaclay.authentication.domain.entity.exception.AuthErrorCode
import com.upsaclay.authentication.domain.entity.exception.AuthenticationException
import com.upsaclay.authentication.domain.usecase.RegisterUseCase
import com.upsaclay.common.domain.usecase.VerifyEmailFormatUseCase
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.entity.ServerCommunicationException
import com.upsaclay.common.domain.extensions.uppercaseFirstLetter
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.IsUserExistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

const val MAX_REGISTRATION_STEP = 3
private const val MIN_PASSWORD_LENGTH = 8

class RegistrationViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val registerUseCase: RegisterUseCase,
    private val isUserExistUseCase: IsUserExistUseCase,
) : ViewModel() {
    private val userId = GenerateIdUseCase()
    private val _screenState = MutableStateFlow(RegistrationScreenState.NOT_REGISTERED)
    val screenState: StateFlow<RegistrationScreenState> = _screenState

    var firstName by mutableStateOf("")
        private set
    var lastName by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    val schoolLevels = listOf("GED 1", "GED 2", "GED 3", "GED 4")
    var schoolLevel by mutableStateOf(schoolLevels[0])
        private set

    fun updateFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun updateLastName(lastName: String) {
        this.lastName = lastName
    }

    fun updateEmail(email: String) {
        this.email = email
    }

    fun updatePassword(password: String) {
        this.password = password
    }

    fun updateSchoolLevel(schoolLevel: String) {
        this.schoolLevel = schoolLevel
    }

    fun resetFirstName() {
        firstName = ""
    }

    fun resetLastName() {
        lastName = ""
    }

    fun resetEmail() {
        email = ""
    }

    fun resetPassword() {
        password = ""
    }

    fun resetSchoolLevel() {
        schoolLevel = schoolLevels[0]
    }

    fun resetScreenState() {
        _screenState.value = RegistrationScreenState.NOT_REGISTERED
    }

    fun resetAllValues() {
        resetFirstName()
        resetLastName()
        resetEmail()
        resetPassword()
        resetSchoolLevel()
        resetScreenState()
    }

    fun register() {
        _screenState.value = RegistrationScreenState.LOADING

        viewModelScope.launch {
            try {
                if (isUserExistUseCase(email.trim())) {
                    _screenState.value = RegistrationScreenState.USER_ALREADY_EXISTS
                    return@launch
                }

                val user = User(
                    id = userId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email.trim(),
                    schoolLevel = schoolLevel
                )

                createUserUseCase(user)
                registerUseCase(email.trim(), password)
                _screenState.value = RegistrationScreenState.REGISTERED
            } catch (e: Exception) {
                _screenState.value = when(e) {
                    is AuthenticationException -> RegistrationScreenState.USER_ALREADY_EXISTS

                    is ServerCommunicationException -> RegistrationScreenState.SERVER_COMMUNICATION_ERROR

                    is IOException -> RegistrationScreenState.USER_CREATION_ERROR

                    else -> RegistrationScreenState.UNKNOWN_ERROR
                }
            }
        }
    }

    fun verifyNamesInputs(): Boolean {
        return if (firstName.isBlank() || lastName.isBlank()) {
            _screenState.value = RegistrationScreenState.EMPTY_FIELDS_ERROR
            false
        } else {
            firstName = firstName.trim().uppercaseFirstLetter()
            lastName = lastName.trim().uppercaseFirstLetter()
            true
        }
    }

    fun validateCredentialInputs() = verifyEmail() && verifyPassword()

    private fun verifyPassword(): Boolean {
        return when {
            password.isBlank() -> {
                _screenState.value = RegistrationScreenState.EMPTY_FIELDS_ERROR
                false
            }

            password.length < MIN_PASSWORD_LENGTH -> {
                _screenState.value = RegistrationScreenState.PASSWORD_LENGTH_ERROR
                false
            }

            else -> true
        }
    }

    private fun verifyEmail(): Boolean {
        return when {
            email.isBlank() -> {
                _screenState.value = RegistrationScreenState.EMPTY_FIELDS_ERROR
                false
            }

            !VerifyEmailFormatUseCase(email.trim()) -> {
                _screenState.value = RegistrationScreenState.EMAIL_FORMAT_ERROR
                false
            }

            else -> true
        }
    }
}