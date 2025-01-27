package com.upsaclay.authentication.domain.entity.exception

enum class AuthErrorCode {
    EMAIL_ALREADY_EXIST,
    INVALID_CREDENTIALS,
    UNKNOWN;

    companion object {
        fun fromCode(code: String): AuthErrorCode {
            return when (code) {
                "ERROR_EMAIL_ALREADY-EXISTS" -> EMAIL_ALREADY_EXIST
                "ERROR_INVALID_CREDENTIAL" -> INVALID_CREDENTIALS
                else -> UNKNOWN
            }
        }
    }
}
