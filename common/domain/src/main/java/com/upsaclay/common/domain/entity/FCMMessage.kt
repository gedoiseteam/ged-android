package com.upsaclay.common.domain.entity

import com.google.gson.annotations.SerializedName

data class FCMMessage<T>(
    val recipientId: String,
    val notification: FCMNotification,
    val data: FCMData<T>,
    val priority: String = "high"
) {
    val icon: String = "ic_notification"
}

data class FCMNotification(
    val title: String,
    val body: String
)

data class FCMData<T>(
    val type: FCMDataType,
    val value: T
)

enum class FCMDataType {
    @SerializedName("message")
    MESSAGE;

    override fun toString(): String {
        return when (this) {
            MESSAGE -> "message"
        }
    }
}