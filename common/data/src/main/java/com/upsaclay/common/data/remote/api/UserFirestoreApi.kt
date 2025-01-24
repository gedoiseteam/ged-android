package com.upsaclay.common.data.remote.api

import com.upsaclay.common.data.remote.UserFirestoreModel
import kotlinx.coroutines.flow.Flow

internal interface UserFirestoreApi {
    suspend fun getUser(userId: Int): UserFirestoreModel?

    suspend fun getUserWithEmail(userEmail: String): UserFirestoreModel?

    suspend fun getUsers(): List<UserFirestoreModel>

    suspend fun createUser(userFirestoreModel: UserFirestoreModel): Result<Unit>

    suspend fun updateProfilePictureUrl(userId: String, profilePictureUrl: String?): Result<Unit>

    suspend fun isUserExist(email: String): Boolean
}