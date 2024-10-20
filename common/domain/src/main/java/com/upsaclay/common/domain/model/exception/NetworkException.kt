package com.upsaclay.common.domain.model.exception

class NetworkException(
    override val message: String? = null,
    override val cause: Throwable? = null
): Exception()