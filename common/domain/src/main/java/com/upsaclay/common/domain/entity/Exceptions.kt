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
): Exception()

class UserNotFoundException(
    override val message: String? = null,
    override val cause: Throwable? = null
): Exception()

class UserAlreadyExist(
    override val message: String? = null,
    override val cause: Throwable? = null
): Exception()

class ForbiddenException(
    override val message: String? = null,
    override val cause: Throwable? = null
): Exception()

interface ErrorType {
    data object TooManyRequestsError: ErrorType
    data object InternalServerError: ErrorType
    data object ServerConnectError: ErrorType
    data object NetworkError: ErrorType
    data object UnknownError: ErrorType
}