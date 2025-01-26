package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository

class SetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) {
        userRepository.setCurrentUser(user)
    }
}