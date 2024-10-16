package com.upsaclay.authentication.domain.model.exception


enum class FirebaseAuthErrorCode {
    EMAIL_ALREADY_EXIST,
    INVALID_CREDENTIALS,
    UNKNOWN;

    companion object {
        fun fromCode(errorCode: String): FirebaseAuthErrorCode {
            return when (errorCode) {
                "auth/email-already-exists" -> EMAIL_ALREADY_EXIST
                else -> {
                    UNKNOWN
                }
            }
        }
    }
}
