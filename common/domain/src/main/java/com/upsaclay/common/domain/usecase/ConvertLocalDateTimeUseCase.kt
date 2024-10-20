package com.upsaclay.common.domain.usecase

import java.time.LocalDateTime
import java.time.ZoneId

object ConvertLocalDateTimeUseCase {
    fun toTimestamp(localDateTime: LocalDateTime): Long {
        return localDateTime.toEpochSecond(ZoneId.systemDefault().rules.getOffset(localDateTime))
    }
}