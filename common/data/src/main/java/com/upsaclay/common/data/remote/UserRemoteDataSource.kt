package com.upsaclay.common.data.remote

import com.upsaclay.common.data.UserMapper
import com.upsaclay.common.data.formatHttpError
import com.upsaclay.common.data.remote.api.UserFirestoreApi
import com.upsaclay.common.data.remote.api.UserRetrofitApi
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

internal class UserRemoteDataSource(
    private val userRetrofitApi: UserRetrofitApi,
    private val userFirestoreApi: UserFirestoreApi
) {
    suspend fun getUser(userId: String): User? = withContext(Dispatchers.IO) {
        userFirestoreApi.getUser(userId)?.let { UserMapper.toDomain(it) }
    }

    suspend fun getUserFlow(userId: String): Flow<User> = withContext(Dispatchers.IO) {
        userFirestoreApi.getUserFlow(userId).mapNotNull {
            it?.let { UserMapper.toDomain(it) }
        }
    }

    suspend fun getUserFirestoreWithEmail(userEmail: String): User? = withContext(Dispatchers.IO) {
        userFirestoreApi.getUserWithEmail(userEmail)?.let { UserMapper.toDomain(it) }
    }

    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        userFirestoreApi.getUsers().map(UserMapper::toDomain)
    }

    suspend fun createUser(user: User) {
        withContext(Dispatchers.IO) {
            launch { createUserWithOracle(user) }
            launch { createUserWithFirestore(user) }
        }
    }

    suspend fun updateProfilePictureUrl(userId: String, url: String) {
        withContext(Dispatchers.IO) {
            launch { userFirestoreApi.updateProfilePictureUrl(userId, url) }
            launch {
                val response = userRetrofitApi.updateProfilePictureUrl(userId, url)
                if (!response.isSuccessful) {
                    val errorMessage = formatHttpError("Error updating profile picture url", response)
                    e(errorMessage)
                    throw IOException(errorMessage)
                }
            }
        }
    }

    suspend fun deleteProfilePictureUrl(userId: String) {
        withContext(Dispatchers.IO) {
            launch { userFirestoreApi.updateProfilePictureUrl(userId, null) }
            launch {
                val response = userRetrofitApi.deleteProfilePictureUrl(userId)
                if (!response.isSuccessful) {
                    val errorMessage = formatHttpError("Error deleting profile picture url", response)
                    e(errorMessage)
                    throw IOException(errorMessage)
                }
            }
        }
    }

    suspend fun isUserExist(email: String): Boolean = withContext(Dispatchers.IO) {
        userFirestoreApi.isUserExist(email)
    }

    private suspend fun createUserWithOracle(user: User) {
        val response = userRetrofitApi.createUser(UserMapper.toDTO(user))
        if (!response.isSuccessful) {
            val errorMessage = formatHttpError("Error creating user with Oracle", response)
            e(errorMessage)
            throw IOException(errorMessage)
        }
    }

    private suspend fun createUserWithFirestore(user: User) {
        userFirestoreApi.createUser(UserMapper.toFirestoreUser(user))
    }
}