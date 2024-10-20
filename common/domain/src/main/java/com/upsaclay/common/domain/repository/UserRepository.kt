package com.upsaclay.common.domain.repository

import com.upsaclay.common.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val currentUserFlow: Flow<User>
    val currentUser: User?
    val users: Flow<List<User>>

    suspend fun getUser(userId: Int): User?

    suspend fun getUser(userEmail: String): User?

    suspend fun createUser(user: User): Result<Int>

    suspend fun setCurrentUser(user: User)

    suspend fun removeCurrentUser()

    suspend fun updateProfilePictureUrl(userId: Int, profilePictureUrl: String): Result<Unit>

    suspend fun deleteProfilePictureUrl(userId: Int): Result<Unit>

    suspend fun isUserExist(email: String): Result<Boolean>
}