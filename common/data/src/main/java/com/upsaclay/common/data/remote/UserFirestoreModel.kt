package com.upsaclay.common.data.remote

import com.google.firebase.firestore.PropertyName
import com.upsaclay.common.data.model.UserDTO
import com.upsaclay.common.domain.model.User

internal data class UserFirestoreModel(
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: Int = -1,

    @get:PropertyName("first_name")
    @set:PropertyName("first_name")
    var firstName: String = "",

    @get:PropertyName("last_name")
    @set:PropertyName("last_name")
    var lastName: String = "",

    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String = "",

    @get:PropertyName("school_level")
    @set:PropertyName("school_level")
    var schoolLevel: String = "",

    @get:PropertyName("is_member")
    @set:PropertyName("is_member")
    var isMember: Boolean = false,

    @get:PropertyName("profile_picture_url")
    @set:PropertyName("profile_picture_url")
    var profilePictureUrl: String? = null,

    @get:PropertyName("is_online")
    @set:PropertyName("is_online")
    var isOnline: Boolean = false
)