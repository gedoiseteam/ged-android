package com.upsaclay.common.domain.entity

import com.upsaclay.common.domain.extensions.uppercaseFirstLetter


data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val schoolLevel: String,
    val isMember: Boolean = false,
    val profilePictureUrl: String? = null
) {
    val fullName: String = "${firstName.uppercaseFirstLetter()} ${lastName.uppercaseFirstLetter()}"
}