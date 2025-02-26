package com.upsaclay.gedoise.presentation.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.DeleteProfilePictureUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.UpdateProfilePictureUseCase
import com.upsaclay.gedoise.domain.entities.AccountScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val deleteProfilePictureUseCase: DeleteProfilePictureUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {
    private val _screenState = MutableStateFlow(AccountScreenState.READ)
    val screenState: StateFlow<AccountScreenState> = _screenState
    val currentUser: StateFlow<User?> = getCurrentUserUseCase()
    var profilePictureUri by mutableStateOf<Uri?>(null)
        private set

    fun updateProfilePictureUri(uri: Uri) {
        profilePictureUri = uri
    }

    fun resetProfilePictureUri() {
        profilePictureUri = null
    }

    fun updateUserProfilePicture() {
        _screenState.value = AccountScreenState.LOADING

        profilePictureUri?.let { uri ->
            viewModelScope.launch {
                try {
                    updateProfilePictureUseCase(uri)
                    _screenState.value = AccountScreenState.PROFILE_PICTURE_UPDATED
                } catch (e: Exception) {
                    _screenState.value = AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR
                }
                resetProfilePictureUri()
            }
        }
    }

    fun deleteUserProfilePicture() {
        _screenState.value = AccountScreenState.LOADING

        viewModelScope.launch {
            val (id, url) = currentUser.value?.id to currentUser.value?.profilePictureUrl
            try {
                deleteProfilePictureUseCase(id!!, url!!)
                _screenState.value = AccountScreenState.PROFILE_PICTURE_DELETED
            } catch (e: Exception) {
                _screenState.value = AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR
            }
            resetProfilePictureUri()
        }
    }

    fun updateScreenState(screenState: AccountScreenState) {
        _screenState.value = screenState
    }

    fun resetScreenState() {
        _screenState.value = AccountScreenState.READ
    }
}