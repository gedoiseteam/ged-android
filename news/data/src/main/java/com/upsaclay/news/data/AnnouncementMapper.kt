package com.upsaclay.news.data

import com.upsaclay.common.domain.UrlUtils.formatProfilePictureUrl
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.news.data.local.model.LocalAnnouncement
import com.upsaclay.news.data.remote.model.RemoteAnnouncement
import com.upsaclay.news.data.remote.model.RemoteAnnouncementWithUser
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState

internal object AnnouncementMapper {
    fun toLocal(announcement: Announcement) = LocalAnnouncement(
        announcementId = announcement.id,
        announcementTitle = announcement.title,
        announcementContent = announcement.content,
        announcementDate = ConvertDateUseCase.toTimestamp(announcement.date),
        announcementState = announcement.state,
        userId = announcement.author.id,
        userFirstName = announcement.author.firstName,
        userLastName = announcement.author.lastName,
        userEmail = announcement.author.email,
        userSchoolLevel = announcement.author.schoolLevel,
        userIsMember = announcement.author.isMember,
        userProfilePictureUrl = announcement.author.profilePictureUrl
    )

    fun toDomain(localAnnouncement: LocalAnnouncement) = Announcement(
        id = localAnnouncement.announcementId,
        title = localAnnouncement.announcementTitle,
        content = localAnnouncement.announcementContent,
        date = ConvertDateUseCase.toLocalDateTime(localAnnouncement.announcementDate),
        author = User(
            id = localAnnouncement.userId,
            firstName = localAnnouncement.userFirstName,
            lastName = localAnnouncement.userLastName,
            email = localAnnouncement.userEmail,
            schoolLevel = localAnnouncement.userSchoolLevel,
            isMember = localAnnouncement.userIsMember,
            profilePictureUrl = localAnnouncement.userProfilePictureUrl
        ),
        state = localAnnouncement.announcementState
    )

    fun toDomain(remoteAnnouncement: RemoteAnnouncementWithUser) = Announcement(
        id = remoteAnnouncement.announcementId,
        title = remoteAnnouncement.announcementTitle,
        content = remoteAnnouncement.announcementContent,
        date = ConvertDateUseCase.toLocalDateTime(remoteAnnouncement.announcementDate),
        author = User(
            id = remoteAnnouncement.userId,
            firstName = remoteAnnouncement.userFirstName,
            lastName = remoteAnnouncement.userLastName,
            email = remoteAnnouncement.userEmail,
            schoolLevel = remoteAnnouncement.userSchoolLevel,
            isMember = remoteAnnouncement.userIsMember == 1,
            profilePictureUrl = formatProfilePictureUrl(remoteAnnouncement.profilePictureFileName)
        ),
        state = AnnouncementState.PUBLISHED
    )

    fun toRemote(announcement: Announcement) = RemoteAnnouncement(
        announcementId = announcement.id,
        announcementTitle = announcement.title,
        announcementContent = announcement.content,
        announcementDate = ConvertDateUseCase.toTimestamp(announcement.date),
        userId = announcement.author.id
    )
}