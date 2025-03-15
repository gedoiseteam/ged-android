package com.upsaclay.authentication.presentation.viewmodels

import android.accounts.NetworkErrorException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthErrorType
import com.upsaclay.authentication.domain.entity.AuthenticationEvent
import com.upsaclay.authentication.domain.entity.exception.AuthUserNotFoundException
import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.SendVerificationEmailUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.domain.entity.TooManyRequestException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class EmailVerificationViewModel(
    val email: String,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase,
    private val setUserAuthenticatedUseCase: SetUserAuthenticatedUseCase,
) : ViewModel() {
    private val _event = MutableSharedFlow<AuthenticationEvent>(replay = 1)
    val event: SharedFlow<AuthenticationEvent> = _event

    fun sendVerificationEmail() {
        viewModelScope.launch {
            try {
                sendVerificationEmailUseCase()
            } catch (e: Exception) {
                _event.emit(handleException(e))
            }
        }
    }

    fun verifyIsEmailVerified() {
        viewModelScope.launch {
            _event.emit(AuthenticationEvent.Loading)
            try {
                if (isEmailVerifiedUseCase()) {
                    _event.emit(AuthenticationEvent.EmailVerified)
                    setUserAuthenticatedUseCase(true)
                } else {
                    _event.emit(AuthenticationEvent.EmailNotVerified)
                }
            } catch (e: Exception) {
                _event.emit(handleException(e))
            }
        }
    }

    private fun handleException(e: Exception): AuthenticationEvent.Error {
        return when (e) {
            is TooManyRequestException -> AuthenticationEvent.Error(ErrorType.TooManyRequestsError)

            is AuthUserNotFoundException -> AuthenticationEvent.Error(AuthErrorType.AUTH_USER_NOT_FOUND)

            is NetworkErrorException -> AuthenticationEvent.Error(ErrorType.NetworkError)

            else -> AuthenticationEvent.Error(ErrorType.UnknownError)
        }
    }
}