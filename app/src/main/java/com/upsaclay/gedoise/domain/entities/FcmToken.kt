package com.upsaclay.gedoise.domain.entities

data class FcmToken(
    val userId: String,
    val value: String,
    val sent: Boolean = false
)
