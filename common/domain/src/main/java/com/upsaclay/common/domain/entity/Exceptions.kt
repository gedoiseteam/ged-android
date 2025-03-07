package com.upsaclay.common.domain.entity

class InternalServerException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception()

class ServerCommunicationException(
    override val message: String? = null,
    override val cause: Throwable? = null,
): Exception()

class TooManyRequestException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception()