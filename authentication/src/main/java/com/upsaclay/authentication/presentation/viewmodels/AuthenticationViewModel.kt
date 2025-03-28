package com.upsaclay.authentication.presentation.viewmodels

import android.accounts.NetworkErrorException
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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

class AuthenticationViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val _event = MutableSharedFlow<AuthenticationEvent>()
    val event: SharedFlow<AuthenticationEvent> = _event
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

    fun login() {
        viewModelScope.launch {
            _event.emit(AuthenticationEvent.Loading)

            try {
                authenticationRepository.loginWithEmailAndPassword(email, password)

                userRepository.getUserWithEmail(email)?.let {
                    userRepository.setCurrentUser(it)
                    if (authenticationRepository.isUserEmailVerified()) {
                        authenticationRepository.setAuthenticated(true)
                    } else {
                        _event.emit(AuthenticationEvent.EmailNotVerified)
                    }
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
        email = ""
    }

    fun resetPassword() {
        password = ""
    }

    fun verifyInputs(): Boolean {
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