package com.upsaclay.authentication.domain.entity

import com.upsaclay.common.domain.entity.ErrorType

sealed class AuthenticationEvent {
    data object Unauthenticated: AuthenticationEvent()
    data object Authenticated: AuthenticationEvent()
    data object Loading: AuthenticationEvent()
    data class Error(val type: ErrorType): AuthenticationEvent()
    data object EmailVerified: AuthenticationEvent()
    data object EmailNotVerified: AuthenticationEvent()
}

enum class AuthErrorType: ErrorType {
    INVALID_CREDENTIALS_ERROR,
    EMPTY_FIELDS_ERROR,
    EMAIL_FORMAT_ERROR,
    AUTH_USER_NOT_FOUND
}