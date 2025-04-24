package com.upsaclay.common.domain

interface FCMNotificationSender {
    suspend fun sendNotification(fcmMessage: String)
}