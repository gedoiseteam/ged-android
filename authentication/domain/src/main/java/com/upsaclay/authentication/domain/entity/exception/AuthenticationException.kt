package com.upsaclay.authentication.domain.entity.exception

class AuthenticationException(
    override val message: String? = null,
    override val cause: Throwable? = null,
    val code: FirebaseAuthErrorCode = FirebaseAuthErrorCode.UNKNOWN
): Exception()