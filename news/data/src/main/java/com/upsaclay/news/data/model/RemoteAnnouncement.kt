package com.upsaclay.news.data.model

import com.google.gson.annotations.SerializedName

internal data class RemoteAnnouncement(
    @SerializedName("ANNOUNCEMENT_ID")
    val announcementId: String,
    @SerializedName("ANNOUNCEMENT_TITLE")
    val announcementTitle: String?,
    @SerializedName("ANNOUNCEMENT_CONTENT")
    val announcementContent: String,
    @SerializedName("ANNOUNCEMENT_DATE")
    val announcementDate: Long,
    @SerializedName("USER_ID")
    val userId: String
)

internal data class RemoteAnnouncementWithUser(
    @SerializedName("ANNOUNCEMENT_ID")
    val announcementId: String,
    @SerializedName("ANNOUNCEMENT_TITLE")
    val announcementTitle: String?,
    @SerializedName("ANNOUNCEMENT_CONTENT")
    val announcementContent: String,
    @SerializedName("ANNOUNCEMENT_DATE")
    val announcementDate: Long,
    @SerializedName("USER_ID")
    val userId: String,
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
)