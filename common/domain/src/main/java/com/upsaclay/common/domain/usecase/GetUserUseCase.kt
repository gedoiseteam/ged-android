package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend fun withId(userId: Int): User? = userRepository.getUserWithFirestore(userId)

    suspend fun withEmail(email: String): User? = userRepository.getUserWithFirestore(email)
}