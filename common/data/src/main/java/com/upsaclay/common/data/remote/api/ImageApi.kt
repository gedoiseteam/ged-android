package com.upsaclay.common.data.remote.api

import okhttp3.Response

internal interface ImageApi {
    suspend fun getImage(url: String): Response
}