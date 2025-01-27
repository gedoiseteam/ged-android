package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend fun withId(userId: String): User? = userRepository.getUser(userId)

    suspend fun withEmail(email: String): User? = userRepository.getUserWithEmail(email)
}