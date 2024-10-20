package com.upsaclay.common.data.remote.api

import com.upsaclay.common.data.model.UserDTO
import com.upsaclay.common.domain.model.ServerResponse.EmptyResponse
import com.upsaclay.common.domain.model.ServerResponse.IntResponse
import com.upsaclay.common.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface UserRetrofitApi {

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): Response<UserDTO>

    @FormUrlEncoded
    @POST("users/get-user-with-email")
    suspend fun getUser(@Field("USER_EMAIL") userEmail: String): Response<UserDTO>

    @POST("users/create")
    suspend fun createUser(@Body user: UserDTO): Response<IntResponse>

    @FormUrlEncoded
    @POST("users/update/profile-picture-url")
    suspend fun updateProfilePictureUrl(
        @Field("USER_ID") userId: Int,
        @Field("USER_PROFILE_PICTURE_URL") userProfilePictureUrl: String
    ): Response<EmptyResponse>

    @DELETE("users/profile-picture-url/{userId}")
    suspend fun deleteProfilePictureUrl(@Path("userId") userId: Int): Response<EmptyResponse>
}