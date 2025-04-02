package com.upsaclay.authentication.domain.entity

import com.upsaclay.common.domain.entity.ErrorType

sealed class RegistrationEvent {
    data object Loading: RegistrationEvent()
    data object Registered: RegistrationEvent()
    data class Error(val type: ErrorType): RegistrationEvent()
}

enum class RegistrationErrorType: ErrorType {
    UNRECOGNIZED_ACCOUNT,
    USER_ALREADY_EXISTS,
    EMPTY_FIELDS_ERROR,
    EMAIL_FORMAT_ERROR,
    PASSWORD_LENGTH_ERROR,
    USER_CREATION_ERROR,
    USER_NOT_WHITE_LISTED_ERROR
}