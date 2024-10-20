package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.repository.UserRepository

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Int> {
        return userRepository.createUser(user)
            .onSuccess { userId ->
                userRepository.setCurrentUser(user.copy(id = userId))
            }
    }
}