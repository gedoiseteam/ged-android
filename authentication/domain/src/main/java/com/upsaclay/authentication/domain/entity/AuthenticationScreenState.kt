package com.upsaclay.authentication.domain.entity

enum class AuthenticationScreenState {
    DEFAULT,
    LOADING,
    EMAIL_VERIFIED,
    EMAIL_NOT_VERIFIED,
    AUTHENTICATION_ERROR,
    EMPTY_FIELDS_ERROR,
    EMAIL_FORMAT_ERROR,
    UNKNOWN_ERROR,
    NETWORK_ERROR,
    TOO_MANY_REQUESTS_ERROR,
    AUTHENTICATED_USER_NOT_FOUND
}