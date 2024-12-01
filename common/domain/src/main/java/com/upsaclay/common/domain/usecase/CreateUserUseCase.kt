package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.repository.UserRepository
import java.util.UUID

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        val uuid = GenerateIDUseCase()
        return userRepository.createUser(user.copy(id = uuid))
            .onSuccess { userRepository.setCurrentUser(user.copy(id = uuid)) }
    }
}