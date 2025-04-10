package com.upsaclay.gedoise.data.api

import com.upsaclay.common.data.remote.ServerResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CredentialsApi {
    @FormUrlEncoded
    @POST("credentials/fcmToken/add")
    suspend fun addFcmToken(
        @Field("userId") userId: String,
        @Field("fcmToken") fcmToken: String
    ): Response<ServerResponse>
}