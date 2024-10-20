package com.upsaclay.news.data.remote.model

import com.google.gson.annotations.SerializedName
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.ConvertLocalDateTimeUseCase
import com.upsaclay.common.domain.usecase.ConvertTimestampUseCase
import com.upsaclay.news.domain.model.Announcement

internal data class AnnouncementRemoteWithUser(
    @SerializedName("ANNOUNCEMENT_ID")
    val announcementId: Int,
    @SerializedName("ANNOUNCEMENT_TITLE")
    val announcementTitle: String?,
    @SerializedName("ANNOUNCEMENT_CONTENT")
    val announcementContent: String,
    @SerializedName("ANNOUNCEMENT_DATE")
    val announcementDate: Long,
    @SerializedName("USER_ID")
    val userId: Int,
    @SerializedName("USER_FIRST_NAME")
    val userFirstName: String,
    @SerializedName("USER_LAST_NAME")
    val userLastName: String,
    @SerializedName("USER_EMAIL")
    val userEmail: String,
    @SerializedName("USER_SCHOOL_LEVEL")
    val userSchoolLevel: String,
    @SerializedName("USER_IS_MEMBER")
    val userIsMember: Int,
    @SerializedName("USER_PROFILE_PICTURE_URL")
    val profilePictureUrl: String?
) {
    fun toDomain() = Announcement(
        id = announcementId,
        title = announcementTitle,
        content = announcementContent,
        date = ConvertTimestampUseCase.toLocalDateTime(announcementDate),
        author = User(
            id = userId,
            firstName = userFirstName,
            lastName = userLastName,
            email = userEmail,
            schoolLevel = userSchoolLevel,
            isMember = userIsMember == 1,
            profilePictureUrl = profilePictureUrl
        )
    )
}

internal data class RemoteAnnouncement(
    @SerializedName("ANNOUNCEMENT_ID")
    val announcementId: Int,
    @SerializedName("ANNOUNCEMENT_TITLE")
    val announcementTitle: String?,
    @SerializedName("ANNOUNCEMENT_CONTENT")
    val announcementContent: String,
    @SerializedName("ANNOUNCEMENT_DATE")
    val announcementDate: Long,
    @SerializedName("USER_ID")
    val userId: Int
) {
    companion object {
        fun fromDomain(announcement: Announcement) = RemoteAnnouncement(
            announcementId = announcement.id,
            announcementTitle = announcement.title,
            announcementContent = announcement.content,
            announcementDate = ConvertLocalDateTimeUseCase.toTimestamp(announcement.date),
            userId = announcement.author.id
        )
    }
}