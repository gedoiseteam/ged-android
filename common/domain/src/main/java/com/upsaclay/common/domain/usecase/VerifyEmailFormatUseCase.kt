package com.upsaclay.common.domain.usecase

object VerifyEmailFormatUseCase {
    private val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    operator fun invoke(text: String): Boolean = emailRegex.matches(text)
}