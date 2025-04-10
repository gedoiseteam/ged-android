package com.upsaclay.common.data

import com.upsaclay.common.data.remote.ServerResponse
import com.upsaclay.common.domain.entity.InternalServerException

class DataIntegrityViolationException(
    override val message: String? = null,
    override val cause: Throwable? = null
): Exception()

fun parseOracleException(serverResponse: ServerResponse?): Exception {
    if (serverResponse == null) {
        return InternalServerException()
    }

    return when (serverResponse.code) {
        "ORA-12801" -> DataIntegrityViolationException()
        else -> InternalServerException()
    }
}