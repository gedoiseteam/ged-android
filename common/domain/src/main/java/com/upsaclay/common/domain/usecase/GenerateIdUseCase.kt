package com.upsaclay.common.domain.usecase

import java.util.UUID
import kotlin.math.absoluteValue

object GenerateIdUseCase {
    val stringId: String
        get() {
            val timestamp = System.currentTimeMillis()
            val uniqueID = "$timestamp-${UUID.randomUUID()}"
            return uniqueID
        }
    val intId: Int
        get() {
            val uuid = UUID.randomUUID()
            return uuid.mostSignificantBits.toInt().absoluteValue
        }
}
