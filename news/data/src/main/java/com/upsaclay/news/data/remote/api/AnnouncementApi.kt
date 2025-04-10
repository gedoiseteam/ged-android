package com.upsaclay.news.data.remote.api

import com.upsaclay.common.data.remote.ServerResponse
import com.upsaclay.news.data.remote.model.RemoteAnnouncement
import com.upsaclay.news.data.remote.model.RemoteAnnouncementWithUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface AnnouncementApi {
    @GET("announcements")
    suspend fun getAnnouncements(): Response<List<RemoteAnnouncementWithUser>>

    @POST("announcements/create")
    suspend fun createAnnouncement(@Body remoteAnnouncement: RemoteAnnouncement): Response<ServerResponse>

    @DELETE("announcements/{id}")
    suspend fun deleteAnnouncement(@Path("id") id: String): Response<ServerResponse>

    @POST("announcements/update")
    suspend fun updateAnnouncement(@Body remoteAnnouncement: RemoteAnnouncement): Response<ServerResponse>
}