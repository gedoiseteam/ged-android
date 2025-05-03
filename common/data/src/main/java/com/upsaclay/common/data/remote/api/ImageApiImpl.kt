package com.upsaclay.common.data.remote.api

import com.upsaclay.common.data.remote.ServerResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

internal class ImageApiImpl(
    private val retrofitImageApi: RetrofitImageApi
): ImageApi {
    override suspend fun getImage(url: String): Response {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        return client.newCall(request).execute()
    }

    override suspend fun uploadImage(image: MultipartBody.Part): retrofit2.Response<ServerResponse> {
        return retrofitImageApi.uploadImage(image)
    }

    override suspend fun deleteImage(filename: String): retrofit2.Response<ServerResponse> {
        return retrofitImageApi.deleteImage(filename)
    }

    internal interface RetrofitImageApi {
        @Multipart
        @POST("image/upload")
        suspend fun uploadImage(@Part image: MultipartBody.Part): retrofit2.Response<ServerResponse>

        @DELETE("image/{filename}")
        suspend fun deleteImage(@Path("filename") filename: String): retrofit2.Response<ServerResponse>
    }
}