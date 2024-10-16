package com.upsaclay.common.domain.repository

import com.upsaclay.common.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: Flow<User?>
    val users: Flow<List<User>>

    suspend fun getUserWithFirestore(userId: Int): User?

    suspend fun getUserWithFirestore(userEmail: String): User?

    suspend fun getUserWithOracle(email: String): User?

    suspend fun createUserWithOracle(user: User): Int?

    suspend fun createUserWithFirestore(user: User): Result<Unit>

    suspend fun setCurrentUser(user: User)

    suspend fun removeCurrentUser()

    suspend fun updateProfilePictureUrl(userId: Int, profilePictureUrl: String): Result<Unit>

    suspend fun deleteProfilePictureUrl(userId: Int): Result<Unit>

    suspend fun isUserExist(email: String): Result<Boolean>
}