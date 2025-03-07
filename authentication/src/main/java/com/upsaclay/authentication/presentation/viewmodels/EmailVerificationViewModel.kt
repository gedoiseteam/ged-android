package com.upsaclay.authentication.presentation.viewmodels

import android.accounts.NetworkErrorException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationScreenState
import com.upsaclay.authentication.domain.entity.exception.AuthUserNotFoundException
import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.SendVerificationEmailUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.common.domain.entity.TooManyRequestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmailVerificationViewModel(
    val email: String,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase,
    private val setUserAuthenticatedUseCase: SetUserAuthenticatedUseCase,
) : ViewModel() {
    private val _screenState = MutableStateFlow(AuthenticationScreenState.DEFAULT)
    val screenState: StateFlow<AuthenticationScreenState> = _screenState

    fun sendVerificationEmail() {
        viewModelScope.launch {
            try {
                sendVerificationEmailUseCase()
            } catch (e: Exception) {
                _screenState.value = when (e) {
                    is TooManyRequestException -> AuthenticationScreenState.TOO_MANY_REQUESTS_ERROR

                    is AuthUserNotFoundException -> AuthenticationScreenState.AUTH_USER_NOT_FOUND

                    is NetworkErrorException -> AuthenticationScreenState.NETWORK_ERROR

                    else -> AuthenticationScreenState.UNKNOWN_ERROR
                }
            }
        }
    }

    fun verifyIsEmailVerified() {
        _screenState.value = AuthenticationScreenState.LOADING

        viewModelScope.launch {
            try {
                if (isEmailVerifiedUseCase()) {
                    _screenState.value = AuthenticationScreenState.EMAIL_VERIFIED
                    setUserAuthenticatedUseCase(true)
                } else {
                    _screenState.value = AuthenticationScreenState.EMAIL_NOT_VERIFIED
                }
            } catch (e: Exception) {
                _screenState.value = when (e) {
                    is TooManyRequestException -> AuthenticationScreenState.TOO_MANY_REQUESTS_ERROR

                    is AuthUserNotFoundException -> AuthenticationScreenState.AUTH_USER_NOT_FOUND

                    is NetworkErrorException -> AuthenticationScreenState.NETWORK_ERROR

                    else -> AuthenticationScreenState.UNKNOWN_ERROR
                }
            }
        }
    }
}