package com.upsaclay.authentication.presentation.viewmodels

import android.accounts.NetworkErrorException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.RegistrationErrorType
import com.upsaclay.authentication.domain.entity.RegistrationEvent
import com.upsaclay.authentication.domain.entity.exception.InvalidCredentialsException
import com.upsaclay.authentication.domain.usecase.RegisterUseCase
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.extensions.uppercaseFirstLetter
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.IsUserExistUseCase
import com.upsaclay.common.domain.usecase.VerifyEmailFormatUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException

private const val MIN_PASSWORD_LENGTH = 8
const val PARIS_SACLAY_DOMAIN = "@universite-paris-saclay.fr"

class RegistrationViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val registerUseCase: RegisterUseCase,
    private val isUserExistUseCase: IsUserExistUseCase,
) : ViewModel() {
    private val userId = GenerateIdUseCase.asString()
    private val _event = MutableSharedFlow<RegistrationEvent>(replay = 1, extraBufferCapacity = 1)
    val event: SharedFlow<RegistrationEvent> = _event

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

    fun resetAllValues() {
        resetFirstName()
        resetLastName()
        resetEmail()
        resetPassword()
        resetSchoolLevel()
    }

    fun register() {
        val email = email.trim() + PARIS_SACLAY_DOMAIN

        viewModelScope.launch {
            _event.emit(RegistrationEvent.Loading)

            try {
                if (isUserExistUseCase(email)) {
                    _event.emit(RegistrationEvent.Error(RegistrationErrorType.USER_ALREADY_EXISTS))
                    return@launch
                }

                val user = User(
                    id = userId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    schoolLevel = schoolLevel
                )

                createUserUseCase(user)
                registerUseCase(email, password)
                _event.emit(RegistrationEvent.Registered)
            } catch (e: Exception) {
                _event.emit(handleException(e))
            }
        }
    }

    fun verifyNamesInputs(): Boolean {
        return if (firstName.isBlank() || lastName.isBlank()) {
            _event.tryEmit(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR))
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
                _event.tryEmit(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR))
                false
            }

            password.length < MIN_PASSWORD_LENGTH -> {
                _event.tryEmit(RegistrationEvent.Error(RegistrationErrorType.PASSWORD_LENGTH_ERROR))
                false
            }

            else -> true
        }
    }

    private fun verifyEmail(): Boolean {
        return when {
            email.isBlank() -> {
                _event.tryEmit(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR))
                false
            }

            !VerifyEmailFormatUseCase(email.trim() + PARIS_SACLAY_DOMAIN) -> {
                _event.tryEmit(RegistrationEvent.Error(RegistrationErrorType.EMAIL_FORMAT_ERROR))
                false
            }

            else -> true
        }
    }

    private fun handleException(e: Exception) : RegistrationEvent.Error {
        return when(e) {
            is InvalidCredentialsException -> RegistrationEvent.Error(RegistrationErrorType.USER_ALREADY_EXISTS)

            is NetworkErrorException -> RegistrationEvent.Error(ErrorType.NetworkError)

            is ConnectException -> RegistrationEvent.Error(ErrorType.ServerConnectError)

            is IOException -> RegistrationEvent.Error(RegistrationErrorType.USER_CREATION_ERROR)

            else -> RegistrationEvent.Error(ErrorType.UnknownError)
        }
    }
}