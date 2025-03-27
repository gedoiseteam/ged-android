package com.upsaclay.common.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

internal class ImageApiImpl: ImageApi {
    override suspend fun getImage(url: String): Response {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        return client.newCall(request).execute()
    }
}