package com.upsaclay.common.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.upsaclay.common.data.formatHttpError
import com.upsaclay.common.data.remote.api.ImageApi
import com.upsaclay.common.data.remote.api.RetrofitImageApi
import com.upsaclay.common.domain.e
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

internal class ImageRemoteDataSource(
    private val retrofitImageApi: RetrofitImageApi,
    private val imageApi: ImageApi
) {
    suspend fun getImage(fileName: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val response = imageApi.getImage(fileName)
            if (response.isSuccessful) {
                    try {
                        val inputStream = response.body?.byteStream() ?: return@withContext null
                        BitmapFactory.decodeStream(inputStream)
                    } catch (e: Exception) {
                        e("Error decoding image: ${e.message}", e)
                        throw IOException()
                    }
            } else {
                e(formatHttpError("Error getting image:", response))
                throw IOException()
            }
        } catch (e: Exception) {
            e("Error getting image: ${e.message}", e)
            throw IOException()
        }
    }

    suspend fun uploadImage(file: File) {
        withContext(Dispatchers.IO) {
            try {
                val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
                retrofitImageApi.uploadImage(multipartBody)
            } catch (e: Exception) {
                e("Error uploading image : ${e.message}", e)
                throw IOException()
            }
        }
    }

    suspend fun deleteImage(imageName: String) {
        withContext(Dispatchers.IO) {
            try {
                retrofitImageApi.deleteImage(imageName)
            } catch (e: Exception) {
                e("Error deleting image: ${e.message}", e)
                throw IOException()
            }
        }
    }
}