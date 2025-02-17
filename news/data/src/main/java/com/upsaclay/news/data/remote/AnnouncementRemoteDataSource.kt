package com.upsaclay.news.data.remote

import com.upsaclay.common.data.formatHttpError
import com.upsaclay.news.data.AnnouncementMapper
import com.upsaclay.news.data.remote.api.AnnouncementApi
import com.upsaclay.news.domain.entity.Announcement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber.Forest.e
import java.io.IOException

internal class AnnouncementRemoteDataSource(private val announcementApi: AnnouncementApi) {
    suspend fun getAnnouncement(): List<Announcement> = withContext(Dispatchers.IO) {
        try {
            val response = announcementApi.getAnnouncements()
            if (response.isSuccessful) {
                val remoteAnnouncements = response.body().takeIf { it != null } ?: emptyList()
                remoteAnnouncements.map(AnnouncementMapper::toDomain)
            } else {
                e(formatHttpError("Error getting remote announcements", response))
                emptyList()
            }
        } catch (e: Exception) {
            e("Error getting remote announcements: ${e.message}")
            emptyList()
        }
    }

    suspend fun createAnnouncement(announcement: Announcement) {
        withContext(Dispatchers.IO) {
            val response = try {
                announcementApi.createAnnouncement(AnnouncementMapper.toRemote(announcement))
            } catch (e: Exception) {
                e("Error creating remote announcement: ${e.message}")
                throw IOException()
            }

            if (!response.isSuccessful) {
                val errorMessage = formatHttpError("Error creating remote announcement", response)
                e(errorMessage)
                throw IOException(errorMessage)
            }
        }
    }

    suspend fun deleteAnnouncement(id: String) {
        withContext(Dispatchers.IO) {
            val response = try {
                announcementApi.deleteAnnouncement(id)
            } catch (e: Exception) {
                e("Error deleting remote announcement: ${e.message}")
                throw IOException()
            }

            if (!response.isSuccessful) {
                val errorMessage = formatHttpError("Error deleting remote announcement", response)
                e(errorMessage)
                throw IOException(errorMessage)
            }
        }
    }

    suspend fun updateAnnouncement(announcement: Announcement) {
        withContext(Dispatchers.IO) {
            val response = try {
                announcementApi.updateAnnouncement(AnnouncementMapper.toRemote(announcement))
            } catch (e: Exception) {
                e("Error updating remote announcement: ${e.message}")
                throw IOException()
            }

            if (!response.isSuccessful) {
                val errorMessage = formatHttpError("Error updating remote announcement", response)
                e(errorMessage)
                throw IOException(errorMessage)
            }
        }
    }
}