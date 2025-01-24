package com.upsaclay.common.data.repository

import com.upsaclay.common.data.UserMapper
import com.upsaclay.common.data.local.UserLocalDataSource
import com.upsaclay.common.data.remote.UserRemoteDataSource
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import okio.IOException

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val scope: CoroutineScope = (GlobalScope + Dispatchers.IO)
): UserRepository {
    private val _currentUserFlow = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUserFlow

    init {
        scope.launch {
            userLocalDataSource.getCurrentUserFlow().collectLatest { userDTO ->
                _currentUserFlow.value = UserMapper.toDomain(userDTO)
            }
        }
    }

    override suspend fun getUsers(): List<User> = userRemoteDataSource.getUsers().map(UserMapper::toDomain)

    override suspend fun getUser(userId: String): User? =
        userRemoteDataSource.getUserFirestoreWithEmail(userId)?.let { UserMapper.toDomain(it) }

    override suspend fun getUserWithEmail(userEmail: String): User? =
        userRemoteDataSource.getUserFirestoreWithEmail(userEmail)?.let { UserMapper.toDomain(it) }

    override suspend fun createUser(user: User): Result<Unit> {
        val userDTO = UserMapper.toDTO(user)
        val userFirestoreModel = UserMapper.toFirestoreModel(user)
        val oracleResult = scope.launch { userRemoteDataSource.createUserWithOracle(userDTO) }
        val firestoreResult = scope.launch { userRemoteDataSource.createUserWithFirestore(userFirestoreModel) }
        return try {
            firestoreResult.join()
            oracleResult.join()
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun setCurrentUser(user: User) {
        userLocalDataSource.setUser(UserMapper.toDTO(user))
    }

    override suspend fun removeCurrentUser() {
        userLocalDataSource.removeCurrentUser()
    }

    override suspend fun updateProfilePictureUrl(userId: String, profilePictureUrl: String): Result<Unit> {
        return userRemoteDataSource.updateProfilePictureUrl(userId, profilePictureUrl)
            .onSuccess { userLocalDataSource.updateProfilePictureUrl(profilePictureUrl) }
    }

    override suspend fun deleteProfilePictureUrl(userId: String): Result<Unit> {
        return userRemoteDataSource.deleteProfilePictureUrl(userId)
            .onSuccess { userLocalDataSource.deleteProfilePictureUrl() }
    }

    override suspend fun isUserExist(email: String): Result<Boolean> = userRemoteDataSource.isUserExist(email)
}