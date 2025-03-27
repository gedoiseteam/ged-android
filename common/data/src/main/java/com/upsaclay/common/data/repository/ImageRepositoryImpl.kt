package com.upsaclay.common.data.repository

import android.graphics.Bitmap
import com.upsaclay.common.data.remote.ImageRemoteDataSource
import com.upsaclay.common.domain.repository.ImageRepository
import java.io.File


internal class ImageRepositoryImpl(
    private val imageRemoteDataSource: ImageRemoteDataSource
): ImageRepository {
    override suspend fun getImage(fileName: String): Bitmap? = imageRemoteDataSource.getImage(fileName)

    override suspend fun uploadImage(file: File) {
        imageRemoteDataSource.uploadImage(file)
    }

    override suspend fun deleteImage(fileName: String) {
        imageRemoteDataSource.deleteImage(fileName)
    }
}