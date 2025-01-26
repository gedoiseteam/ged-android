package com.upsaclay.authentication.domain.entity.exception

class TooManyRequestException(
    override val message: String? = null,
    override val cause: Throwable? = null
): Exception()