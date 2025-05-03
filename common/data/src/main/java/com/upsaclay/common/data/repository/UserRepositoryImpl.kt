package com.upsaclay.common.data.repository

import android.accounts.NetworkErrorException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.upsaclay.common.data.DataIntegrityViolationException
import com.upsaclay.common.data.local.UserLocalDataSource
import com.upsaclay.common.data.remote.UserRemoteDataSource
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.DuplicateUserException
import com.upsaclay.common.domain.entity.ForbiddenException
import com.upsaclay.common.domain.entity.InternalServerException
import com.upsaclay.common.domain.entity.TooManyRequestException
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    scope: CoroutineScope
) : UserRepository {
    private val _user = MutableStateFlow<User?>(null)
    override val user: Flow<User?> = _user
    override val currentUser: User?
        get() = _user.value

    init {
        scope.launch {
            userLocalDataSource.getCurrentUserFlow().collectLatest { user ->
                _user.value = user
            }
        }
    }

    override suspend fun getUsers(): List<User> {
        return try {
            userRemoteDataSource.getUsers()
        }
        catch (e: FirebaseNetworkException) {
            e("Error network connection: ${e.message}", e)
            throw NetworkErrorException()
        }
        catch (e: FirebaseTooManyRequestsException) {
            e("Error to get users with Firestore: ${e.message}", e)
            throw TooManyRequestException()
        }
        catch (e: Exception) {
            e("Error getting users: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getUser(userId: String): User? {
        return try {
            userRemoteDataSource.getUser(userId)
        }
        catch (e: FirebaseNetworkException) {
            e("Error network connection: ${e.message}", e)
            throw NetworkErrorException()
        }
        catch (e: FirebaseTooManyRequestsException) {
            e("Error to get user with Firestore: ${e.message}", e)
            throw TooManyRequestException()
        }
        catch (e: Exception) {
            e("Error getting user: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getCurrentUser(): User? = userLocalDataSource.getCurrentUser()

    override suspend fun getUserFlow(userId: String): Flow<User> {
        return try {
            userRemoteDataSource.getUserFlow(userId)
        }
        catch (e: FirebaseNetworkException) {
            e("Error network connection: ${e.message}", e)
            throw NetworkErrorException()
        }
        catch (e: FirebaseTooManyRequestsException) {
            e("Error to get user with Firestore: ${e.message}", e)
            throw TooManyRequestException()
        }
        catch (e: Exception) {
            e("Error getting user: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getUserWithEmail(userEmail: String): User? {
        return try {
            userRemoteDataSource.getUserFirestoreWithEmail(userEmail)
        }
        catch (e: FirebaseNetworkException) {
            e("Error network connection: ${e.message}", e)
            throw NetworkErrorException()
        }
        catch (e: FirebaseTooManyRequestsException) {
            e("Error to get user with Firestore: ${e.message}", e)
            throw TooManyRequestException()
        }
        catch (e: Exception) {
            e("Error getting user with email: ${e.message}", e)
            throw e
        }
    }

    override suspend fun createUser(user: User) {
        try {
            userRemoteDataSource.createUser(user)
            userLocalDataSource.setCurrentUser(user)
        }
        catch (e: FirebaseNetworkException) {
            e("Error network connection: ${e.message}", e)
            throw NetworkErrorException()
        }
        catch (e: FirebaseTooManyRequestsException) {
            e("Error to create user with Firestore: ${e.message}", e)
            throw TooManyRequestException()
        }
        catch (e: DataIntegrityViolationException) {
            e("Error to create user with Oracle: ${e.message}", e)
            throw DuplicateUserException()
        }
        catch (e: Exception) {
            e("Error creating user: ${e.message}", e)
            throw e
        }
    }

    override suspend fun setCurrentUser(user: User) {
        userLocalDataSource.setCurrentUser(user)
    }

    override suspend fun deleteCurrentUser() {
        _user.value = null
        userLocalDataSource.deleteCurrentUser()
    }

    override suspend fun updateProfilePictureFileName(userId: String, fileName: String) {
        try {
            userRemoteDataSource.updateProfilePictureFileName(userId, fileName)
            userLocalDataSource.updateProfilePictureFileName(fileName)
        } catch (e: FirebaseNetworkException) {
            e("Error network connection: ${e.message}", e)
            throw NetworkErrorException()
        }
        catch (e: FirebaseTooManyRequestsException) {
            e("Error to update profile picture file name with Firestore: ${e.message}", e)
            throw TooManyRequestException()
        }
        catch (e: Exception) {
            e("Error to update profile picture file name: ${e.message}", e)
            throw e
        }
    }

    override suspend fun deleteProfilePictureUrl(userId: String) {
        userRemoteDataSource.deleteProfilePictureUrl(userId)
        userLocalDataSource.deleteProfilePictureUrl()
    }

    override suspend fun isUserExist(email: String): Boolean {
        return try {
            userRemoteDataSource.isUserExist(email)
        }
        catch (e: FirebaseNetworkException) {
            e("Error network connection: ${e.message}", e)
            throw NetworkErrorException()
        }
        catch (e: FirebaseTooManyRequestsException) {
            e("Error to check is user exist with Firestore: ${e.message}", e)
            throw TooManyRequestException()
        }
        catch (e: Exception) {
            e("Error to verify is user exist : ${e.message}", e)
            throw e
        }
    }
}