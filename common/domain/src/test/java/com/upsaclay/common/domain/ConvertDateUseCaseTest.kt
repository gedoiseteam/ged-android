package com.upsaclay.common.domain

import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import java.time.Instant
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ConvertDateUseCaseTest {
    private val convertDateUseCase = ConvertDateUseCase
    private val localDateTime = LocalDateTime.of(2025, 2, 4, 15, 30)
    private val instant = Instant.parse("2025-02-04T15:30:00Z")
    private val timestamp = Instant.parse("2025-02-04T15:30:00Z").toEpochMilli()

    @Test
    fun toLocalDateTime_should_convert_instant_to_localDateTime() {
        // When
        val result = convertDateUseCase.toLocalDateTime(instant)

        // Then
        assertEquals(localDateTime, result)
    }

    @Test
    fun toLocalDateTime_should_convert_timestamp_to_localDateTime() {
        // When
        val result = convertDateUseCase.toLocalDateTime(timestamp)

        // Then
        assertEquals(localDateTime, result)
    }

    @Test
    fun toInstant_should_convert_localDateTime_to_instant() {
        // When
        val result = convertDateUseCase.toInstant(localDateTime)

        // Then
        assertEquals(instant, result)
    }

    @Test
    fun toTimestamp_should_convert_localDateTime_to_timestamp() {
        // When
        val result = convertDateUseCase.toTimestamp(localDateTime)

        // Then
        assertEquals(timestamp, result)
    }
}