package com.upsaclay.authentication.domain.entity

enum class RegistrationScreenState {
    OK,
    ERROR,
    LOADING,
    REGISTERED,
    NOT_REGISTERED,
    UNRECOGNIZED_ACCOUNT,
    USER_ALREADY_EXIST,
    EMPTY_FIELDS_ERROR,
    EMAIL_FORMAT_ERROR,
    PASSWORD_LENGTH_ERROR
}