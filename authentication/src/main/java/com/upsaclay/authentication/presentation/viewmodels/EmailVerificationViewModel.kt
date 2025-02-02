package com.upsaclay.authentication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.AuthenticationScreenState
import com.upsaclay.authentication.domain.entity.exception.TooManyRequestException
import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.SendVerificationEmailUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmailVerificationViewModel(
    val email: String,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase,
    private val setUserAuthenticatedUseCase: SetUserAuthenticatedUseCase
) : ViewModel() {
    private val _screenState = MutableStateFlow(AuthenticationScreenState.DEFAULT)
    val screenState: StateFlow<AuthenticationScreenState> = _screenState

    fun sendVerificationEmail() {
        viewModelScope.launch {
            try {
                sendVerificationEmailUseCase()
            } catch (e: Exception) {
                when (e) {
                    is TooManyRequestException -> _screenState.value =
                        AuthenticationScreenState.TOO_MANY_REQUESTS_ERROR

                    else -> _screenState.value = AuthenticationScreenState.UNKNOWN_ERROR
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
                _screenState.value = AuthenticationScreenState.UNKNOWN_ERROR
            }
        }
    }
}