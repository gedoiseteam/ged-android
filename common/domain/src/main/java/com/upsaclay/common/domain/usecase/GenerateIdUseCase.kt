package com.upsaclay.common.domain.usecase

import java.util.UUID

object GenerateIdUseCase {
    operator fun invoke(): String {
        val timestamp = System.currentTimeMillis()
        val uniqueID = "$timestamp-${UUID.randomUUID()}"
        return uniqueID
    }
}
