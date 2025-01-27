package com.upsaclay.common.domain.usecase

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object ConvertDateUseCase {
    fun toLocalDateTime(instant: Instant): LocalDateTime =
        LocalDateTime.ofInstant(instant, ZoneOffset.UTC)

    fun toLocalDateTime(timestamp: Long): LocalDateTime =
        Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).toLocalDateTime()

    fun toInstant(localDateTime: LocalDateTime): Instant =
        localDateTime.atZone(ZoneOffset.UTC).toInstant()

    fun toTimestamp(localDateTime: LocalDateTime): Long =
        localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
}