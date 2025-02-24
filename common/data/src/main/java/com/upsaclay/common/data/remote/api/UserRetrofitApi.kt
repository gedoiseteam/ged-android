package com.upsaclay.common.data.remote.api

import com.upsaclay.common.data.entity.UserDTO
import com.upsaclay.common.domain.entity.ServerResponse.EmptyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface UserRetrofitApi {

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): Response<UserDTO>

    @FormUrlEncoded
    @POST("users/get-user-with-email")
    suspend fun getUserWithEmail(@Field("USER_EMAIL") userEmail: String): Response<UserDTO>

    @POST("users/create")
    suspend fun createUser(@Body user: UserDTO): Response<Unit>

    @FormUrlEncoded
    @PUT("users/profile-picture-url")
    suspend fun updateProfilePictureUrl(
        @Field("USER_ID") userId: String,
        @Field("USER_PROFILE_PICTURE_URL") userProfilePictureUrl: String
    ): Response<EmptyResponse>

    @DELETE("users/profile-picture-url/{userId}")
    suspend fun deleteProfilePictureUrl(@Path("userId") userId: String): Response<EmptyResponse>
}