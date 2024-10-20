package com.upsaclay.common.domain.usecase

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object LocalDateTimeFormatterUseCase {
    fun formatDayMonthYear(localDateTime: LocalDateTime): String {
        val formatter = if (Locale.getDefault().language == "fr") {
            DateTimeFormatter.ofPattern("dd MMMyyyy", Locale.FRENCH)
        } else {
            DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
        }
        return localDateTime.format(formatter)
    }
}