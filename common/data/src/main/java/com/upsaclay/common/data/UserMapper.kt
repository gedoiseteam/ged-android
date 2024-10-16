package com.upsaclay.common.data

import com.upsaclay.common.data.model.UserDTO
import com.upsaclay.common.data.remote.UserFirestoreModel
import com.upsaclay.common.domain.model.User

internal object UserMapper {
    fun toDTO(user: User) = UserDTO(
        userId = if (user.id == -1) null else user.id,
        userFirstName = user.firstName,
        userLastName = user.lastName,
        userEmail = user.email,
        userSchoolLevel = user.schoolLevel,
        userIsMember = if (user.isMember) 1 else 0,
        userProfilePictureUrl = user.profilePictureUrl
    )

    fun toFirestoreModel(user: User) = UserFirestoreModel(
        userId = user.id,
        firstName = user.firstName,
        lastName = user.lastName,
        email = user.email,
        schoolLevel = user.schoolLevel,
        isMember = user.isMember,
        profilePictureUrl = user.profilePictureUrl
    )

    fun toFirestoreModel(userDTO: UserDTO) = UserFirestoreModel(
        userId = userDTO.userId ?: -1,
        firstName = userDTO.userFirstName,
        lastName = userDTO.userLastName,
        email = userDTO.userEmail,
        schoolLevel = userDTO.userSchoolLevel,
        isMember = userDTO.userIsMember == 1,
        profilePictureUrl = userDTO.userProfilePictureUrl
    )

    fun toDomain(userDTO: UserDTO) = User(
        id = userDTO.userId ?: -1,
        firstName = userDTO.userFirstName,
        lastName = userDTO.userLastName,
        email = userDTO.userEmail,
        schoolLevel = userDTO.userSchoolLevel,
        isMember = userDTO.userIsMember == 1,
        profilePictureUrl = userDTO.userProfilePictureUrl
    )

    fun toDomain(userFirestoreModel: UserFirestoreModel) = User(
        id = userFirestoreModel.userId,
        firstName = userFirestoreModel.firstName,
        lastName = userFirestoreModel.lastName,
        email = userFirestoreModel.email,
        schoolLevel = userFirestoreModel.schoolLevel,
        isMember = userFirestoreModel.isMember,
        profilePictureUrl = userFirestoreModel.profilePictureUrl
    )
}