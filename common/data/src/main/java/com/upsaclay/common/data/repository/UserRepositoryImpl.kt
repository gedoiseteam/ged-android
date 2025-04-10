package com.upsaclay.common.data.repository

import android.accounts.NetworkErrorException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.upsaclay.common.data.DataIntegrityViolationException
import com.upsaclay.common.data.local.UserLocalDataSource
import com.upsaclay.common.data.remote.UserRemoteDataSource
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.ForbiddenException
import com.upsaclay.common.domain.entity.TooManyRequestException
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.entity.UserAlreadyExist
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    scope: CoroutineScope
) : UserRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser

    init {
        scope.launch {
            userLocalDataSource.getCurrentUserFlow().collectLatest { user ->
                _currentUser.value = user
            }
        }
    }

    override suspend fun getUsers(): List<User> = userRemoteDataSource.getUsers()

    override suspend fun getUser(userId: String): User? = userRemoteDataSource.getUser(userId)

    override suspend fun getCurrentUser(): User? = userLocalDataSource.getCurrentUser()

    override suspend fun getUserFlow(userId: String): Flow<User> = userRemoteDataSource.getUserFlow(userId)

    override suspend fun getUserWithEmail(userEmail: String): User? = userRemoteDataSource.getUserFirestoreWithEmail(userEmail)

    override suspend fun getFilteredUsers(userName: String): List<User> = userRemoteDataSource.getFilteredUsers(userName)

    override suspend fun createUser(user: User) {
        try {
            userRemoteDataSource.createUser(user)
            userLocalDataSource.setCurrentUser(user)
        } catch (e: ConnectException) {
            e("Connect exception: ${e.message}", e)
            throw e
        } catch (e: FirebaseNetworkException) {
            e("Error network connection: ${e.message}", e)
            throw NetworkErrorException()
        } catch (e: FirebaseTooManyRequestsException) {
            e("Error to create user with Firestore: ${e.message}", e)
            throw TooManyRequestException()
        } catch (e: ForbiddenException) {
            e("Error to create user with Oracle: ${e.message}", e)
            throw ForbiddenException()
        } catch (e: DataIntegrityViolationException) {
            e("Error to create user with Oracle: ${e.message}", e)
            throw UserAlreadyExist()
        } catch (e: Exception) {
            e("Error creating user: ${e.message}", e)
            throw IOException()
        }
    }

    override suspend fun setCurrentUser(user: User) {
        userLocalDataSource.setCurrentUser(user)
    }

    override suspend fun deleteCurrentUser() {
        _currentUser.value = null
        userLocalDataSource.deleteCurrentUser()
    }

    override suspend fun updateProfilePictureUrl(userId: String, profilePictureUrl: String) {
        userRemoteDataSource.updateProfilePictureFileName(userId, profilePictureUrl)
        userLocalDataSource.updateProfilePictureUrl(profilePictureUrl)
    }

    override suspend fun deleteProfilePictureUrl(userId: String) {
        userRemoteDataSource.deleteProfilePictureUrl(userId)
        userLocalDataSource.deleteProfilePictureUrl()
    }

    override suspend fun isUserExist(email: String): Boolean {
        try {
            return userRemoteDataSource.isUserExist(email)
        } catch (e: FirebaseNetworkException) {
            e("Error network connection: ${e.message}", e)
            throw NetworkErrorException()
        } catch (e: FirebaseTooManyRequestsException) {
            e("Error to check is user exist with Firestore: ${e.message}", e)
            throw TooManyRequestException()
        } catch (e: Exception) {
            e("Error to verify email with Firebase: ${e.message}", e)
            throw IOException()
        }
    }
}