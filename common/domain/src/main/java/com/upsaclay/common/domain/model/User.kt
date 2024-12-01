package com.upsaclay.common.domain.model

import com.upsaclay.common.domain.uppercaseFirstLetter

data class User(
    val id: String = "",
    val firstName: String,
    val lastName: String,
    val email: String,
    val schoolLevel: String,
    val isMember: Boolean = false,
    val profilePictureUrl: String? = null
) {
    val fullName: String = "${firstName.uppercaseFirstLetter()} ${lastName.uppercaseFirstLetter()}"

    fun isUpdated(user: User): Boolean = this.id == user.id &&
        this.profilePictureUrl != user.profilePictureUrl ||
        this.isMember != user.isMember ||
        this.schoolLevel != user.schoolLevel
}