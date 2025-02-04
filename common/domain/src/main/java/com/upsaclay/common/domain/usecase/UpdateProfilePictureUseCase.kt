package com.upsaclay.common.domain.usecase

import android.net.Uri
import com.upsaclay.common.domain.formatProfilePictureUrl
import com.upsaclay.common.domain.repository.FileRepository
import com.upsaclay.common.domain.repository.ImageRepository
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import java.io.IOException

class UpdateProfilePictureUseCase(
    private val fileRepository: FileRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(profilePictureUri: Uri) {
        val currentUser = userRepository.currentUser.first() ?: throw IOException("No user logged in")

        val currentTime = System.currentTimeMillis()
        val fileName = "${currentUser.id}-profile-picture-$currentTime"
        val file = fileRepository.createFileFromUri(fileName, profilePictureUri)
        val url = formatProfilePictureUrl(fileName, file.extension)

        imageRepository.uploadImage(file)
        userRepository.updateProfilePictureUrl(currentUser.id, url)
        currentUser.profilePictureUrl?.let { deletePreviousProfilePicture(it) }
    }

    private suspend fun deletePreviousProfilePicture(userProfilePictureUrl: String) {
        val fileName = userProfilePictureUrl.substringAfterLast("/")
        imageRepository.deleteImage(fileName)
    }
}