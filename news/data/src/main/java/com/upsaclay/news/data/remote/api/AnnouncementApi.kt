package com.upsaclay.news.data.remote.api

import com.upsaclay.common.domain.model.ServerResponse.EmptyResponse
import com.upsaclay.common.domain.model.ServerResponse.IntResponse
import com.upsaclay.news.data.remote.model.AnnouncementRemoteWithUser
import com.upsaclay.news.data.remote.model.RemoteAnnouncement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface AnnouncementApi {
    @GET("announcements")
    suspend fun getAllAnnouncement(): Response<List<AnnouncementRemoteWithUser>>

    @POST("announcements/create")
    suspend fun createAnnouncement(@Body remoteAnnouncement: RemoteAnnouncement): Response<IntResponse>

    @DELETE("announcements/{id}")
    suspend fun deleteAnnouncement(@Path("id") id: String): Response<EmptyResponse>

    @POST("announcements/update")
    suspend fun updateAnnouncement(@Body remoteAnnouncement: RemoteAnnouncement): Response<EmptyResponse>
}