package com.upsaclay.common.data.remote.api

import com.upsaclay.common.data.remote.ServerResponse
import okhttp3.MultipartBody
import okhttp3.Response

interface ImageApi {
    suspend fun getImage(url: String): Response

    suspend fun uploadImage(image: MultipartBody.Part): retrofit2.Response<ServerResponse>

    suspend fun deleteImage(filename: String): retrofit2.Response<ServerResponse>
}