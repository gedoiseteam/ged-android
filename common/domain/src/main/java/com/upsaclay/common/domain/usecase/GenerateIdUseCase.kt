package com.upsaclay.common.domain.usecase

import java.util.UUID

object GenerateIDUseCase {
    operator fun invoke(): String = UUID.randomUUID().toString()
}