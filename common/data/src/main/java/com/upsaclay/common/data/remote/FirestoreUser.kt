package com.upsaclay.common.data.remote

import com.google.firebase.firestore.PropertyName
import com.upsaclay.common.data.UserField

internal data class FirestoreUser(
    @get:PropertyName(UserField.Remote.USER_ID)
    @set:PropertyName(UserField.Remote.USER_ID)
    var userId: String = "",

    @get:PropertyName(UserField.Remote.FIRST_NAME)
    @set:PropertyName(UserField.Remote.FIRST_NAME)
    var firstName: String = "",

    @get:PropertyName(UserField.Remote.LAST_NAME)
    @set:PropertyName(UserField.Remote.LAST_NAME)
    var lastName: String = "",

    @get:PropertyName(UserField.Remote.FULL_NAME)
    @set:PropertyName(UserField.Remote.FULL_NAME)
    var fullName: String = "",

    @get:PropertyName(UserField.Remote.EMAIL)
    @set:PropertyName(UserField.Remote.EMAIL)
    var email: String = "",

    @get:PropertyName(UserField.Remote.SCHOOL_LEVEL)
    @set:PropertyName(UserField.Remote.SCHOOL_LEVEL)
    var schoolLevel: String = "",

    @get:PropertyName(UserField.Remote.IS_MEMBER)
    @set:PropertyName(UserField.Remote.IS_MEMBER)
    var isMember: Boolean = false,

    @get:PropertyName(UserField.Remote.PROFILE_PICTURE_FILE_NAME)
    @set:PropertyName(UserField.Remote.PROFILE_PICTURE_FILE_NAME)
    var profilePictureFileName: String? = null,

    @get:PropertyName(UserField.Remote.IS_ONLINE)
    @set:PropertyName(UserField.Remote.IS_ONLINE)
    var isOnline: Boolean = false,
)