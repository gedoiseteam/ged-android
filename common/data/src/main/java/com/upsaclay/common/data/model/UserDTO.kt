package com.upsaclay.common.data.model

import com.google.gson.annotations.SerializedName
import com.upsaclay.common.domain.model.User

internal data class UserDTO(
    @SerializedName("USER_ID") val userId: String? = null,
    @SerializedName("USER_FIRST_NAME") val userFirstName: String,
    @SerializedName("USER_LAST_NAME") val userLastName: String,
    @SerializedName("USER_EMAIL") val userEmail: String,
    @SerializedName("USER_SCHOOL_LEVEL") val userSchoolLevel: String,
    @SerializedName("USER_IS_MEMBER") val userIsMember: Int = 0,
    @SerializedName("USER_PROFILE_PICTURE_URL") val userProfilePictureUrl: String? = null
)