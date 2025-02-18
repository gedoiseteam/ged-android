package com.upsaclay.authentication.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationScreenState
import com.upsaclay.authentication.domain.entity.exception.AuthenticationException
import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.LoginUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.common.domain.entity.ServerCommunicationException
import com.upsaclay.common.domain.entity.TooManyRequestException
import com.upsaclay.common.domain.usecase.GetUserUseCase
import com.upsaclay.common.domain.usecase.SetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.VerifyEmailFormatUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException

class AuthenticationViewModel(
    private val loginUseCase: LoginUseCase,
    private val setUserAuthenticatedUseCase: SetUserAuthenticatedUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val setCurrentUserUseCase: SetCurrentUserUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase
) : ViewModel() {
    private val _screenState = MutableStateFlow(AuthenticationScreenState.DEFAULT)
    val screenState: StateFlow<AuthenticationScreenState> = _screenState
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
        _screenState.value = AuthenticationScreenState.LOADING

        viewModelScope.launch {
            try {
                loginUseCase(email, password)
                
                getUserUseCase.withEmail(email)?.let {
                    setCurrentUserUseCase(it)
                    if (isEmailVerifiedUseCase()) {
                        setUserAuthenticatedUseCase(true)
                        _screenState.value = AuthenticationScreenState.DEFAULT
                    } else {
                        _screenState.value = AuthenticationScreenState.EMAIL_NOT_VERIFIED
                    }
                } ?: run {
                    _screenState.value = AuthenticationScreenState.AUTHENTICATED_USER_NOT_FOUND
                }
            } catch (e: Exception) {
                _screenState.value = when (e) {
                    is TooManyRequestException -> AuthenticationScreenState.TOO_MANY_REQUESTS_ERROR

                    is AuthenticationException -> AuthenticationScreenState.AUTHENTICATION_ERROR

                    is ServerCommunicationException, is IOException -> AuthenticationScreenState.SERVER_COMMUNICATION_ERROR

                    else -> AuthenticationScreenState.UNKNOWN_ERROR
                }
            }
        }
    }

    fun resetEmail() {
        email = ""
    }

    fun resetPassword() {
        password = ""
    }

    fun resetScreenState() {
        _screenState.value = AuthenticationScreenState.DEFAULT
    }

    fun verifyInputs(): Boolean {
        return when {
            email.isBlank() || password.isBlank() -> {
                _screenState.value = AuthenticationScreenState.EMPTY_FIELDS_ERROR
                return false
            }

            !VerifyEmailFormatUseCase(email) -> {
                _screenState.value = AuthenticationScreenState.EMAIL_FORMAT_ERROR
                return false
            }

            else -> true
        }
    }
}