package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.repository.UserRepository
import java.io.IOException

class CreateNewUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.createUserWithOracle(user)?.let { userId ->
            userRepository.createUserWithFirestore(user.copy(id = userId))
                .onSuccess {
                    userRepository.setCurrentUser(user.copy(id = userId))
                }
        } ?: Result.failure(IOException())
    }
}