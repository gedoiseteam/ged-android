package com.upsaclay.authentication.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.domain.entity.exception.AuthenticationException
import com.upsaclay.authentication.domain.entity.exception.AuthErrorCode
import com.upsaclay.authentication.domain.entity.exception.TooManyRequestException
import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.LoginUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.common.domain.entity.exception.NetworkException
import com.upsaclay.common.domain.usecase.GetUserUseCase
import com.upsaclay.common.domain.usecase.SetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.VerifyEmailFormatUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val loginUseCase: LoginUseCase,
    private val setUserAuthenticatedUseCase: SetUserAuthenticatedUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val setCurrentUserUseCase: SetCurrentUserUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase
) : ViewModel() {
    private val _authenticationState = MutableStateFlow(AuthenticationState.UNAUTHENTICATED)
    val authenticationState: StateFlow<AuthenticationState> = _authenticationState
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
        _authenticationState.value = AuthenticationState.LOADING

        viewModelScope.launch(Dispatchers.IO) {
            try {
                loginUseCase(email, password)
                getUserUseCase.withEmail(email)?.let {
                    if (isEmailVerifiedUseCase()) {
                        setCurrentUserUseCase(it)
                        setUserAuthenticatedUseCase(true)
                        _authenticationState.value = AuthenticationState.AUTHENTICATED
                    } else {
                        _authenticationState.value = AuthenticationState.EMAIL_NOT_VERIFIED
                    }
                } ?: run {
                    _authenticationState.value = AuthenticationState.AUTHENTICATED_USER_NOT_FOUND
                }
            } catch (e: Exception) {
                _authenticationState.value = when(e) {
                    is NetworkException -> AuthenticationState.NETWORK_ERROR

                    is TooManyRequestException -> AuthenticationState.TOO_MANY_REQUESTS_ERROR

                    is AuthenticationException -> {
                        if(e.code == AuthErrorCode.INVALID_CREDENTIALS) {
                            AuthenticationState.AUTHENTICATION_ERROR
                        } else {
                            AuthenticationState.UNKNOWN_ERROR
                        }
                    }

                    else -> AuthenticationState.UNKNOWN_ERROR
                }
            }
        }
    }

    fun resetAuthenticationState() {
        _authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun verifyInputs(): Boolean {
        return when {
            email.isBlank() || password.isBlank() -> {
                _authenticationState.value = AuthenticationState.INPUTS_EMPTY_ERROR
                return false
            }

            !VerifyEmailFormatUseCase(email) -> {
                _authenticationState.value = AuthenticationState.EMAIL_FORMAT_ERROR
                return false
            }

            else -> true
        }
    }
}