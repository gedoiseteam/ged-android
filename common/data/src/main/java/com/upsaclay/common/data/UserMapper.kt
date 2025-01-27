package com.upsaclay.common.data

import com.upsaclay.common.data.entity.UserDTO
import com.upsaclay.common.data.remote.FirestoreUser
import com.upsaclay.common.domain.entity.User

internal object UserMapper {
    fun toDTO(user: User) = UserDTO(
        userId = if (user.id == "") null else user.id,
        userFirstName = user.firstName,
        userLastName = user.lastName,
        userEmail = user.email,
        userSchoolLevel = user.schoolLevel,
        userIsMember = if (user.isMember) 1 else 0,
        userProfilePictureUrl = user.profilePictureUrl
    )

    fun toFirestoreUser(user: User) = FirestoreUser(
        userId = user.id,
        firstName = user.firstName,
        lastName = user.lastName,
        email = user.email,
        schoolLevel = user.schoolLevel,
        isMember = user.isMember,
        profilePictureUrl = user.profilePictureUrl
    )

    fun toFirestoreUser(userDTO: UserDTO) = FirestoreUser(
        userId = userDTO.userId ?: "",
        firstName = userDTO.userFirstName,
        lastName = userDTO.userLastName,
        email = userDTO.userEmail,
        schoolLevel = userDTO.userSchoolLevel,
        isMember = userDTO.userIsMember == 1,
        profilePictureUrl = userDTO.userProfilePictureUrl
    )

    fun toDomain(userDTO: UserDTO) = User(
        id = userDTO.userId ?: "",
        firstName = userDTO.userFirstName,
        lastName = userDTO.userLastName,
        email = userDTO.userEmail,
        schoolLevel = userDTO.userSchoolLevel,
        isMember = userDTO.userIsMember == 1,
        profilePictureUrl = userDTO.userProfilePictureUrl
    )

    fun toDomain(firestoreUser: FirestoreUser) = User(
        id = firestoreUser.userId,
        firstName = firestoreUser.firstName,
        lastName = firestoreUser.lastName,
        email = firestoreUser.email,
        schoolLevel = firestoreUser.schoolLevel,
        isMember = firestoreUser.isMember,
        profilePictureUrl = firestoreUser.profilePictureUrl
    )
}