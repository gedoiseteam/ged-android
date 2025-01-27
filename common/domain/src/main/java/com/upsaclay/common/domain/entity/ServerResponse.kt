package com.upsaclay.common.domain.entity

sealed class ServerResponse {
    data class EmptyResponse(val message: String, val error: String?) : ServerResponse()
}