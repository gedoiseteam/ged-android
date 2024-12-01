package com.upsaclay.common.data.remote

import com.google.firebase.firestore.FirebaseFirestoreException
import com.upsaclay.common.data.model.UserDTO
import com.upsaclay.common.data.remote.api.UserFirestoreApi
import com.upsaclay.common.data.remote.api.UserRetrofitApi
import com.upsaclay.common.domain.e
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

internal class UserRemoteDataSource(
    private val userRetrofitApi: UserRetrofitApi,
    private val userFirestoreApi: UserFirestoreApi
) {
    suspend fun getUserFirestore(userId: String): UserFirestoreModel? = withContext(Dispatchers.IO) {
        try {
            userFirestoreApi.getUserWithEmail(userId)
        } catch (e: IOException) {
            e("Error getting user: ${e.message}")
            null
        } catch (e: FirebaseFirestoreException) {
            e("Error getting user: ${e.message}")
            null
        }
    }

    suspend fun getUserFirestoreWithEmail(userEmail: String): UserFirestoreModel? = withContext(Dispatchers.IO) {
        try {
            userFirestoreApi.getUserWithEmail(userEmail)
        } catch (e: IOException) {
            e("Error getting user: ${e.message}")
            null
        } catch (e: FirebaseFirestoreException) {
            e("Error getting user: ${e.message}")
            null
        }
    }

    suspend fun getAllUsers(): Flow<List<UserFirestoreModel>> = withContext(Dispatchers.IO) {
        userFirestoreApi.getAllUsers()
    }

    suspend fun createUserWithOracle(userDTO: UserDTO) {
        withContext(Dispatchers.IO) {
            try {
                userRetrofitApi.createUser(userDTO).body()
            } catch (e: IOException) {
                e("Error creating user with Oracle: ${e.message}", e)
                null
            }
        }
    }

    suspend fun createUserWithFirestore(userFirestoreModel: UserFirestoreModel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            userFirestoreApi.createUser(userFirestoreModel)
        } catch (e: IOException) {
            e("Error creating user with Firestore: ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseFirestoreException) {
            e("Error creating user with Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateProfilePictureUrl(userId: String, newProfilePictureUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            userRetrofitApi.updateProfilePictureUrl(userId, newProfilePictureUrl)
            userFirestoreApi.updateProfilePictureUrl(userId, newProfilePictureUrl)
        } catch (e: IOException) {
            e("Error updating user profile picture url: ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseFirestoreException) {
            e("Error updating user profile picture url: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteProfilePictureUrl(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            userRetrofitApi.deleteProfilePictureUrl(userId)
            userFirestoreApi.updateProfilePictureUrl(userId, null)
        } catch (e: IOException) {
            e("Error deleting user profile picture url: ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseFirestoreException) {
            e("Error deleting user profile picture url: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun isUserExist(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            Result.success(userFirestoreApi.isUserExist(email))
        }
        catch (e: IOException) {
            e("Error isUserExist : ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseFirestoreException) {
            e("Error isUserExist : ${e.message}", e)
            Result.failure(e)
        }
    }
}