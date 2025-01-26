package com.upsaclay.common.data.repository

import com.upsaclay.common.data.formatHttpError
import com.upsaclay.common.data.remote.ImageRemoteDataSource
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.i
import com.upsaclay.common.domain.repository.ImageRepository
import java.io.File
import java.io.IOException

internal class ImageRepositoryImpl(private val imageRemoteDataSource: ImageRemoteDataSource) :
    ImageRepository {

    override suspend fun uploadImage(file: File) {
        val response = imageRemoteDataSource.uploadImage(file)
        if (!response.isSuccessful) {
            val errorMessage = formatHttpError("Error to upload image ${file.name}", response)
            e(errorMessage)
            throw IOException(errorMessage)
        }
    }

    override suspend fun deleteImage(fileName: String) {
        val response = imageRemoteDataSource.deleteImage(fileName)
        if (!response.isSuccessful) {
            val errorMessage = formatHttpError("Error to delete image $fileName", response)
            e(errorMessage)
            throw IOException(errorMessage)
        }
    }
}