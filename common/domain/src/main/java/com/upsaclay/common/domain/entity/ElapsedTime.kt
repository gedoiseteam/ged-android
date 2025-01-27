package com.upsaclay.common.domain.entity

import java.time.LocalDateTime

sealed class ElapsedTime {
    data class Now(val value: Long) : ElapsedTime()

    data class Minute(val value: Long) : ElapsedTime()

    data class Hour(val value: Long) : ElapsedTime()

    data class Day(val value: Long) : ElapsedTime()

    data class Week(val value: Long) : ElapsedTime()

    data class Later(val value: LocalDateTime) : ElapsedTime()
}