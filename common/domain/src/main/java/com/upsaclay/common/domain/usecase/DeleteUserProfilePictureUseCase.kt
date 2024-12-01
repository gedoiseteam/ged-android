package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.repository.ImageRepository
import com.upsaclay.common.domain.repository.UserRepository

class DeleteUserProfilePictureUseCase(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(userId: String, currentProfilePictureUrl: String): Result<Unit> {
        val urlDeletionResult = userRepository.deleteProfilePictureUrl(userId)

        return if (urlDeletionResult.isSuccess) {
            val fileName = currentProfilePictureUrl.substringAfterLast("/")
            imageRepository.deleteImage(fileName)
        } else {
            urlDeletionResult
        }
    }
}