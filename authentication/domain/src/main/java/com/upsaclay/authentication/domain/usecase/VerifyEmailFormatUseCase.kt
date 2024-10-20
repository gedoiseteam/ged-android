package com.upsaclay.authentication.domain.usecase

class VerifyEmailFormatUseCase {
    private val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

    operator fun invoke(text: String): Boolean = emailRegex.matches(text)
}