package com.upsaclay.common.data.repository

import com.upsaclay.common.data.local.UserLocalDataSource
import com.upsaclay.common.data.remote.UserRemoteDataSource
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

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

    override suspend fun getUserFlow(userId: String): Flow<User> = userRemoteDataSource.getUserFlow(userId)

    override suspend fun getUserWithEmail(userEmail: String): User? = userRemoteDataSource.getUserFirestoreWithEmail(userEmail)

    override suspend fun createUser(user: User) {
        userRemoteDataSource.createUser(user)
        userLocalDataSource.setCurrentUser(user)
    }

    override suspend fun setCurrentUser(user: User) {
        userLocalDataSource.setCurrentUser(user)
    }

    override suspend fun deleteCurrentUser() {
        _currentUser.value = null
        userLocalDataSource.deleteCurrentUser()
    }

    override suspend fun updateProfilePictureUrl(userId: String, profilePictureUrl: String) {
        userRemoteDataSource.updateProfilePictureUrl(userId, profilePictureUrl)
        userLocalDataSource.updateProfilePictureUrl(profilePictureUrl)
    }

    override suspend fun deleteProfilePictureUrl(userId: String) {
        userRemoteDataSource.deleteProfilePictureUrl(userId)
        userLocalDataSource.deleteProfilePictureUrl()
    }

    override suspend fun isUserExist(email: String): Boolean = userRemoteDataSource.isUserExist(email)
}