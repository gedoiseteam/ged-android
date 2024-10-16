package com.upsaclay.common.domain.usecase

import android.net.Uri
import com.upsaclay.common.domain.formatProfilePictureUrl
import com.upsaclay.common.domain.i
import com.upsaclay.common.domain.repository.FileRepository
import com.upsaclay.common.domain.repository.ImageRepository
import com.upsaclay.common.domain.repository.UserRepository
import java.io.IOException

class UpdateUserProfilePictureUseCase(
    private val fileRepository: FileRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(profilePictureUri: Uri): Result<Unit> {
        val currentUser = userRepository.currentUser ?: return Result.failure(IllegalArgumentException("User not logged in"))

        val currentTime = System.currentTimeMillis()
        val fileName = "${currentUser.id}-profile-picture-$currentTime"
        val profilePictureFile = fileRepository.createFileFromUri(fileName, profilePictureUri)
        val uploadImageResult = imageRepository.uploadImage(profilePictureFile)

        return if (uploadImageResult.isSuccess) {
            val profilePictureUrl = formatProfilePictureUrl(fileName, profilePictureFile.extension)

            userRepository.updateProfilePictureUrl(currentUser.id, profilePictureUrl)
                .onSuccess { currentUser.profilePictureUrl?.let { deleteProfilePictureImage(it) } }
        } else {
            val exception = uploadImageResult.exceptionOrNull() ?: IOException("Error uploading image")
            Result.failure(exception)
        }
    }

    private suspend fun deleteProfilePictureImage(userProfilePictureUrl: String): Result<Unit> {
        val fileName = userProfilePictureUrl.substringAfterLast("/")
        return imageRepository.deleteImage(fileName)
    }
}