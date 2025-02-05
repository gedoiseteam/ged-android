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
import com.upsaclay.common.domain.extensions.uppercaseFirstLetter
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import com.upsaclay.common.domain.usecase.IsUserExistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val registerUseCase: RegisterUseCase,
    private val isUserExistUseCase: IsUserExistUseCase,
) : ViewModel() {
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

                val userId = registerUseCase(email.trim(), password)

                val user = User(
                    id = userId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email.trim(),
                    schoolLevel = schoolLevel
                )

                createUserUseCase(user)
                _screenState.value = RegistrationScreenState.REGISTERED
            } catch (e: Exception) {
                if (e is AuthenticationException) {
                    _screenState.value = when (e.code) {
                        AuthErrorCode.EMAIL_ALREADY_AFFILIATED -> RegistrationScreenState.USER_ALREADY_EXISTS
                        else -> RegistrationScreenState.ERROR
                    }
                } else {
                    _screenState.value = RegistrationScreenState.ERROR
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

    fun validateCredentialInputs() = verifyPassword() && verifyEmail()

    private fun verifyPassword(): Boolean {
        return when {
            password.isBlank() -> {
                _screenState.value = RegistrationScreenState.EMPTY_FIELDS_ERROR
                false
            }

            password.length <= 8 -> {
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