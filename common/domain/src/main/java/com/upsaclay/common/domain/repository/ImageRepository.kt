package com.upsaclay.common.domain.repository

import android.graphics.Bitmap
import java.io.File

interface ImageRepository {
    suspend fun getImage(fileName: String): Bitmap?

    suspend fun uploadImage(file: File)

    suspend fun deleteImage(fileName: String)
}