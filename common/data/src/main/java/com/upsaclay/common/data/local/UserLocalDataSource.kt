package com.upsaclay.common.data.local

import com.upsaclay.common.data.model.UserDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

internal class UserLocalDataSource(private val userDataStore: UserDataStore) {

    fun getCurrentUserFlow(): Flow<UserDTO> = userDataStore.getCurrentUserFlow().filterNotNull()

    suspend fun getCurrentUser(): UserDTO? = userDataStore.getCurrentUser()

    suspend fun setUser(userDTO: UserDTO) {
        userDataStore.storeCurrentUser(userDTO)
    }

    suspend fun updateProfilePictureUrl(profilePictureUrl: String) {
        userDataStore.getCurrentUser()?.let { userDTO ->
            userDataStore.storeCurrentUser(userDTO.copy(userProfilePictureUrl = profilePictureUrl))
        }
    }

    suspend fun deleteProfilePictureUrl() {
        userDataStore.getCurrentUser()?.let { userDTO ->
            userDataStore.storeCurrentUser(userDTO.copy(userProfilePictureUrl = null))
        }
    }

    suspend fun removeCurrentUser() {
        userDataStore.removeCurrentUser()
    }
}