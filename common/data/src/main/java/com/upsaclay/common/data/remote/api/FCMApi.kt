package com.upsaclay.common.data.remote.api

import com.upsaclay.common.data.remote.ServerResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FCMApi {
    @FormUrlEncoded
    @POST("fcm/addToken")
    suspend fun addToken(
        @Field("userId") userId: String,
        @Field("token") value: String
    ): Response<ServerResponse>

    @FormUrlEncoded
    @POST("fcm/sendNotification")
    suspend fun sendNotification(@Field("fcmMessage") fcmMessage: String): Response<ServerResponse>
}