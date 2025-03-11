package com.upsaclay.common.domain.usecase

import java.util.UUID
import kotlin.math.absoluteValue

object GenerateIdUseCase {
    fun asString(): String {
        val timestamp = System.currentTimeMillis()
        val uniqueID = "$timestamp-${UUID.randomUUID()}"
        return uniqueID
    }

    fun asInt(): Int {
        val uuid = UUID.randomUUID()
        return uuid.mostSignificantBits.toInt().absoluteValue
    }
}
