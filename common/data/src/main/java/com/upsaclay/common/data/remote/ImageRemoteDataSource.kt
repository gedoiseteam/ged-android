package com.upsaclay.common.data.remote

import com.upsaclay.common.data.remote.api.ImageApi
import com.upsaclay.common.domain.e
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

internal class ImageRemoteDataSource(private val imageApi: ImageApi) {
    suspend fun uploadImage(file: File) {
        withContext(Dispatchers.IO) {
            try {
                val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
                imageApi.uploadImage(multipartBody)
            } catch (e: Exception) {
                e("Error uploading image : ${e.message}", e)
                throw IOException()
            }
        }
    }

    suspend fun deleteImage(imageName: String) {
        withContext(Dispatchers.IO) {
            try {
                imageApi.deleteImage(imageName)
            } catch (e: Exception) {
                e("Error deleting image: ${e.message}", e)
                throw IOException()
            }
        }
    }
}