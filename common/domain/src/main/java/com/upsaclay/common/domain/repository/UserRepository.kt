package com.upsaclay.common.domain.repository

import com.upsaclay.common.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUserFlow: StateFlow<User?>
    val users: Flow<List<User>>

    suspend fun getUser(userId: String): User?

    suspend fun getUserWithEmail(userEmail: String): User?

    suspend fun createUser(user: User): Result<Unit>

    suspend fun setCurrentUser(user: User)

    suspend fun removeCurrentUser()

    suspend fun updateProfilePictureUrl(userId: String, profilePictureUrl: String): Result<Unit>

    suspend fun deleteProfilePictureUrl(userId: String): Result<Unit>

    suspend fun isUserExist(email: String): Result<Boolean>
}