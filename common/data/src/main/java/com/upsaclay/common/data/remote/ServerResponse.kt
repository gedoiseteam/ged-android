package com.upsaclay.common.data.remote

data class ServerResponse(
    val message: String,
    val code: String?,
    val error: String?
)