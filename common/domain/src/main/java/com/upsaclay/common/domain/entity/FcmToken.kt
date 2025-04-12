package com.upsaclay.common.domain.entity

data class FcmToken(
    val userId: String,
    val value: String,
    val sent: Boolean = false
)
