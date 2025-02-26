package com.upsaclay.common.domain.usecase

import android.net.Uri
import com.upsaclay.common.domain.UrlUtils.getFileNameFromUrl
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

        val fileName = "${currentUser.id}-profile-picture-${System.currentTimeMillis()}"
        val file = fileRepository.createFileFromUri(fileName, profilePictureUri)

        imageRepository.uploadImage(file)
        userRepository.updateProfilePictureUrl(currentUser.id, file.name)
        currentUser.profilePictureUrl?.let { deletePreviousProfilePicture(it) }
    }

    private suspend fun deletePreviousProfilePicture(userProfilePictureUrl: String) {
        getFileNameFromUrl(userProfilePictureUrl)?.let { imageRepository.deleteImage(it) }
    }
}