package com.upsaclay.authentication.domain.entity.exception

class AuthenticationException(
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Exception()

class UserAlreadyExistsException(
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Exception()