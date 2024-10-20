package com.upsaclay.common.domain.usecase

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

object ConvertTimestampUseCase {
    fun toLocalDateTime(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC)
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
    }
}