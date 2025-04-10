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
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.domain.entity.ForbiddenException
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.entity.UserAlreadyExist
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.VerifyEmailFormatUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException

private const val MIN_PASSWORD_LENGTH = 8

class ThirdRegistrationViewModel(
    private val firstName: String,
    private val lastName: String,
    private val schoolLevel: String,
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val _event = MutableSharedFlow<RegistrationEvent>()
    val event: SharedFlow<RegistrationEvent> = _event

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    fun updateEmail(email: String) {
        this.email = email
    }

    fun updatePassword(password: String) {
        this.password = password
    }

    fun register() {
        val email = email.trim()

        viewModelScope.launch {
            _event.emit(RegistrationEvent.Loading)

            try {
                if (userRepository.isUserExist(email)) {
                    _event.emit(RegistrationEvent.Error(RegistrationErrorType.USER_ALREADY_EXISTS))
                    return@launch
                }

                val user = User(
                    id = GenerateIdUseCase.asString(),
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    schoolLevel = schoolLevel
                )

                userRepository.createUser(user)
                authenticationRepository.registerWithEmailAndPassword(email, password)
                authenticationRepository.setAuthenticated(true)
                _event.emit(RegistrationEvent.Registered)
            } catch (e: Exception) {
                _event.emit(handleException(e))
            }
        }
    }

    fun validateCredentialInputs() = verifyEmail() && verifyPassword()

    private fun verifyPassword(): Boolean {
        return when {
            password.isBlank() -> {
                viewModelScope.launch { _event.emit(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR)) }
                false
            }

            password.length < MIN_PASSWORD_LENGTH -> {
                viewModelScope.launch { _event.emit(RegistrationEvent.Error(RegistrationErrorType.PASSWORD_LENGTH_ERROR)) }
                false
            }

            else -> true
        }
    }

    private fun verifyEmail(): Boolean {
        return when {
            email.isBlank() -> {
                viewModelScope.launch { _event.emit(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR)) }
                false
            }

            !VerifyEmailFormatUseCase(email.trim()) -> {
                viewModelScope.launch { _event.emit(RegistrationEvent.Error(RegistrationErrorType.EMAIL_FORMAT_ERROR)) }
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

            is ForbiddenException -> RegistrationEvent.Error(RegistrationErrorType.USER_NOT_WHITE_LISTED_ERROR)

            is UserAlreadyExist -> RegistrationEvent.Error(RegistrationErrorType.USER_ALREADY_EXISTS)

            is IOException -> RegistrationEvent.Error(RegistrationErrorType.USER_CREATION_ERROR)

            else -> RegistrationEvent.Error(ErrorType.UnknownError)
        }
    }
}