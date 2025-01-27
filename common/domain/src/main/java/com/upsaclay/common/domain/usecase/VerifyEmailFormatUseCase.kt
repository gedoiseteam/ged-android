package com.upsaclay.common.domain.usecase

object VerifyEmailFormatUseCase {
    operator fun invoke(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }
}