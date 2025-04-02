package com.upsaclay.message.domain.usecase

import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.extensions.uppercaseFirstLetter
import com.upsaclay.common.domain.repository.UserRepository

class GetFilteredUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userName: String): List<User> =
        userRepository.getFilteredUsers(formatUserName(userName))

    private fun formatUserName(userName: String): String =
        userName
            .trim()
            .split(" ")
            .joinToString(" ") { it.uppercaseFirstLetter() }
}