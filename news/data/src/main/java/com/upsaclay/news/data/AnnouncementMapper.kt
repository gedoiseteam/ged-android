package com.upsaclay.news.data

import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.ConvertLocalDateTimeUseCase
import com.upsaclay.common.domain.usecase.ConvertTimestampUseCase
import com.upsaclay.news.data.model.ANNOUNCEMENTS_TABLE
import com.upsaclay.news.data.model.RemoteAnnouncementWithUser
import com.upsaclay.news.data.model.LocalAnnouncement
import com.upsaclay.news.data.model.RemoteAnnouncement
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState

internal object AnnouncementMapper {
    fun toLocal(announcement: Announcement) = LocalAnnouncement(
        announcementId = announcement.id,
        announcementTitle = announcement.title,
        announcementContent = announcement.content,
        announcementDate = ConvertLocalDateTimeUseCase.toTimestamp(announcement.date),
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
        date = ConvertTimestampUseCase.toLocalDateTime(localAnnouncement.announcementDate),
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
        date = ConvertTimestampUseCase.toLocalDateTime(remoteAnnouncement.announcementDate),
        author = User(
            id = remoteAnnouncement.userId,
            firstName = remoteAnnouncement.userFirstName,
            lastName = remoteAnnouncement.userLastName,
            email = remoteAnnouncement.userEmail,
            schoolLevel = remoteAnnouncement.userSchoolLevel,
            isMember = remoteAnnouncement.userIsMember == 1,
            profilePictureUrl = remoteAnnouncement.profilePictureUrl
        ),
        state = AnnouncementState.CREATED
    )

    fun toRemote(announcement: Announcement) = RemoteAnnouncement(
        announcementId = announcement.id,
        announcementTitle = announcement.title,
        announcementContent = announcement.content,
        announcementDate = ConvertLocalDateTimeUseCase.toTimestamp(announcement.date),
        userId = announcement.author.id
    )
}