package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): StateFlow<User?> = userRepository.currentUser
}