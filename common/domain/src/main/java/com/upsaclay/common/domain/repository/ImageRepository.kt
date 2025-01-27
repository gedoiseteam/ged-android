package com.upsaclay.common.domain.repository

import java.io.File

interface ImageRepository {
    suspend fun uploadImage(file: File)

    suspend fun deleteImage(fileName: String)
}