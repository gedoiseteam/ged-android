package com.upsaclay.authentication.domain.model.exception

class AuthenticationException(
    override val message: String? = null,
    override val cause: Throwable? = null,
    val code: FirebaseAuthErrorCode = FirebaseAuthErrorCode.UNKNOWN
): Exception()