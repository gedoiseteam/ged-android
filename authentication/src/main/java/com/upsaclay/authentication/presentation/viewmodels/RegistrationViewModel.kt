package com.upsaclay.authentication.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.RegistrationState
import com.upsaclay.authentication.domain.entity.exception.AuthErrorCode
import com.upsaclay.authentication.domain.entity.exception.AuthenticationException
import com.upsaclay.authentication.domain.usecase.RegisterUseCase
import com.upsaclay.authentication.domain.usecase.VerifyEmailFormatUseCase
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.extensions.uppercaseFirstLetter
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import com.upsaclay.common.domain.usecase.IsUserExistUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val registerUseCase: RegisterUseCase,
    private val verifyEmailFormatUseCase: VerifyEmailFormatUseCase,
    private val isUserExistUseCase: IsUserExistUseCase,
) : ViewModel() {
    private val _registrationState = MutableStateFlow(RegistrationState.NOT_REGISTERED)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    var firstName by mutableStateOf("")
        private set
    var lastName by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    val schoolLevels = persistentListOf("GED 1", "GED 2", "GED 3", "GED 4")
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

    fun resetRegistrationState() {
        _registrationState.value = RegistrationState.NOT_REGISTERED
    }

    fun register() {
        _registrationState.value = RegistrationState.LOADING

        viewModelScope.launch {
            try {
                if(isUserExistUseCase(email.trim())) {
                    _registrationState.value = RegistrationState.USER_ALREADY_EXIST
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
                _registrationState.value = RegistrationState.REGISTERED
            } catch (e: Exception) {
                if(e is AuthenticationException) {
                    _registrationState.value = when(e.code) {
                        AuthErrorCode.EMAIL_ALREADY_EXIST -> RegistrationState.USER_ALREADY_EXIST
                        else -> RegistrationState.ERROR
                    }
                } else {
                    _registrationState.value = RegistrationState.ERROR
                }
            }
        }
    }

    fun verifyNamesInputs(): Boolean {
        return if(firstName.isBlank() || lastName.isBlank()) {
            _registrationState.value = RegistrationState.INPUTS_EMPTY_ERROR
            false
        } else {
            firstName = firstName.uppercaseFirstLetter().trim()
            lastName = lastName.uppercaseFirstLetter().trim()
            true
        }
    }

    fun validateCredentialInputs() = verifyPassword() && verifyEmail()

    private fun verifyPassword(): Boolean {
        return when {
            password.isBlank() -> {
                _registrationState.value = RegistrationState.INPUTS_EMPTY_ERROR
                false
            }

            password.length <= 8 -> {
                _registrationState.value = RegistrationState.PASSWORD_LENGTH_ERROR
                false
            }

            else -> true
        }
    }

    private fun verifyEmail(): Boolean {
        return when {
            email.isBlank() -> {
                _registrationState.value = RegistrationState.INPUTS_EMPTY_ERROR
                false
            }

            !verifyEmailFormatUseCase(email.trim()) -> {
                _registrationState.value = RegistrationState.EMAIL_FORMAT_ERROR
                true
            }

            else -> true
        }
    }
}