package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): List<User> = userRepository.getUsers()
}