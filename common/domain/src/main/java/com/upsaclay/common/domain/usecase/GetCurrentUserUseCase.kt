package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class GetCurrentUserUseCase(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<User?> = userRepository.currentUser
}