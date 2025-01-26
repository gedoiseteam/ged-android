package com.upsaclay.profile.presentation.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.DeleteProfilePictureUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.UpdateUserProfilePictureUseCase
import com.upsaclay.profile.domain.entities.AccountScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AccountViewModel(
    private val updateUserProfilePictureUseCase: UpdateUserProfilePictureUseCase,
    private val deleteUserProfilePictureUseCase: DeleteProfilePictureUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _accountScreenState = MutableStateFlow(AccountScreenState.READ)
    val accountScreenState: StateFlow<AccountScreenState> = _accountScreenState
    val currentUser: StateFlow<User?> = getCurrentUserUseCase()
    var profilePictureUri by mutableStateOf<Uri?>(null)
        private set

    fun updateProfilePictureUri(uri: Uri) {
        profilePictureUri = uri
    }

    fun updateAccountScreenState(screenState: AccountScreenState) {
        _accountScreenState.value = screenState
    }

    fun resetProfilePictureUri() {
        profilePictureUri = null
    }

    fun updateUserProfilePicture() {
        _accountScreenState.value = AccountScreenState.LOADING

        profilePictureUri?.let { uri ->
            viewModelScope.launch {
                try {
                    updateUserProfilePictureUseCase(uri)
                    _accountScreenState.value = AccountScreenState.PROFILE_PICTURE_UPDATED
                } catch (e: Exception) {
                    _accountScreenState.value = AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR
                }
                resetProfilePictureUri()
            }
        }
    }

    fun deleteUserProfilePicture() {
        _accountScreenState.value = AccountScreenState.LOADING

        viewModelScope.launch {
            val (id, url) = currentUser.first()?.id to currentUser.first()?.profilePictureUrl
            try {
                deleteUserProfilePictureUseCase(id!!, url!!)
                _accountScreenState.value = AccountScreenState.PROFILE_PICTURE_UPDATED
            } catch (e: Exception) {
                _accountScreenState.value = AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR
            }
            resetProfilePictureUri()
        }
    }
}