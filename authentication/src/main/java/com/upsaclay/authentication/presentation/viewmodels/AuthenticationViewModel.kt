package com.upsaclay.authentication.presentation.viewmodels

import android.accounts.NetworkErrorException
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthErrorType
import com.upsaclay.authentication.domain.entity.AuthenticationEvent
import com.upsaclay.authentication.domain.entity.exception.InvalidCredentialsException
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.domain.entity.TooManyRequestException
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.VerifyEmailFormatUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class AuthenticationViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val _event = MutableSharedFlow<AuthenticationEvent>()
    val event: SharedFlow<AuthenticationEvent> = _event
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun login() {
        val email = _email.value
        val password = _password.value

        viewModelScope.launch {
            _event.emit(AuthenticationEvent.Loading)

            try {
                authenticationRepository.loginWithEmailAndPassword(email, password)

                userRepository.getUserWithEmail(email)?.let {
                    userRepository.setCurrentUser(it)
                    authenticationRepository.setAuthenticated(true)
                } ?: run {
                    _event.emit(AuthenticationEvent.Error(AuthErrorType.AUTH_USER_NOT_FOUND))
                    resetPassword()
                }
            } catch (e: Exception) {
                _event.emit(handleException(e))
            }
        }
    }

    fun resetEmail() {
        _email.value = ""
    }

    fun resetPassword() {
        _password.value = ""
    }

    fun verifyInputs(): Boolean {
        val email = _email.value
        val password = _password.value

        return when {
            email.isBlank() || password.isBlank() -> {
                viewModelScope.launch { _event.emit(AuthenticationEvent.Error(AuthErrorType.EMPTY_FIELDS_ERROR)) }
                return false
            }

            !VerifyEmailFormatUseCase(email) -> {
               viewModelScope.launch { _event.emit(AuthenticationEvent.Error(AuthErrorType.EMAIL_FORMAT_ERROR)) }
                return false
            }

            else -> true
        }
    }

    private fun handleException(e: Exception): AuthenticationEvent.Error {
        return when (e) {
            is TooManyRequestException -> AuthenticationEvent.Error(ErrorType.TooManyRequestsError)

            is InvalidCredentialsException -> {
                resetPassword()
                AuthenticationEvent.Error(AuthErrorType.INVALID_CREDENTIALS_ERROR)
            }

            is IOException -> {
                resetPassword()
                AuthenticationEvent.Error(ErrorType.InternalServerError)
            }

            is NetworkErrorException -> {
                resetPassword()
                AuthenticationEvent.Error(ErrorType.NetworkError)
            }

            else -> {
                resetPassword()
                AuthenticationEvent.Error(ErrorType.UnknownError)
            }
        }
    }
}