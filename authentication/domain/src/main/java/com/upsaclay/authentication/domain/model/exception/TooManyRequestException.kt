package com.upsaclay.authentication.domain.model.exception

class TooManyRequestException(
    override val message: String? = null,
    override val cause: Throwable? = null
): Exception()