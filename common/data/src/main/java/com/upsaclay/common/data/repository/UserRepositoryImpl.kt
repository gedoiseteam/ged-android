package com.upsaclay.common.data.repository

import com.upsaclay.common.data.UserMapper
import com.upsaclay.common.data.local.UserLocalDataSource
import com.upsaclay.common.data.remote.UserRemoteDataSource
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import okio.IOException

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
): UserRepository {
    private val _currentUserFlow = MutableStateFlow<User?>(null)
    override val currentUserFlow: Flow<User> = _currentUserFlow.filterNotNull()
    override val currentUser: User? get() = _currentUserFlow.value

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: Flow<List<User>> = _users

    init {
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                userLocalDataSource.getCurrentUserFlow().collectLatest { userDTO ->
                    _currentUserFlow.value = UserMapper.toDomain(userDTO)
                }
            }
            launch {
                userRemoteDataSource.getAllUsers().collectLatest {
                    _users.value = it.map(UserMapper::toDomain)
                }
            }
        }
    }

    override suspend fun getUser(userId: Int): User? =
        userRemoteDataSource.getUserWithFirestore(userId)?.let { UserMapper.toDomain(it) }

    override suspend fun getUser(userEmail: String): User? =
        userRemoteDataSource.getUserWithFirestore(userEmail)?.let { UserMapper.toDomain(it) }

    override suspend fun createUser(user: User): Result<Int> {
        val userDTO = UserMapper.toDTO(user)
        val userId = userRemoteDataSource.createUserWithOracle(userDTO)
        return userId?.let {
            val userFirestoreModel = UserMapper.toFirestoreModel(user.copy(id = it))
            userRemoteDataSource.createUserWithFirestore(userFirestoreModel)
            Result.success(it)
        } ?: Result.failure(IOException())
    }

    override suspend fun setCurrentUser(user: User) {
        userLocalDataSource.setUser(UserMapper.toDTO(user))
    }

    override suspend fun removeCurrentUser() {
        userLocalDataSource.removeCurrentUser()
    }

    override suspend fun updateProfilePictureUrl(userId: Int, profilePictureUrl: String): Result<Unit> {
        return userRemoteDataSource.updateProfilePictureUrl(userId, profilePictureUrl)
            .onSuccess { userLocalDataSource.updateProfilePictureUrl(profilePictureUrl) }
    }

    override suspend fun deleteProfilePictureUrl(userId: Int): Result<Unit> {
        return userRemoteDataSource.deleteProfilePictureUrl(userId)
            .onSuccess { userLocalDataSource.deleteProfilePictureUrl() }
    }

    override suspend fun isUserExist(email: String): Result<Boolean> = userRemoteDataSource.isUserExist(email)
}