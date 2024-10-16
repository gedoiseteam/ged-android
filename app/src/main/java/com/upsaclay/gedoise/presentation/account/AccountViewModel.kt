package com.upsaclay.gedoise.presentation.account

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.usecase.DeleteUserProfilePictureUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.UpdateUserProfilePictureUseCase
import com.upsaclay.gedoise.data.account.AccountScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AccountViewModel(
    private val updateUserProfilePictureUseCase: UpdateUserProfilePictureUseCase,
    private val deleteUserProfilePictureUseCase: DeleteUserProfilePictureUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _accountScreenState = MutableStateFlow(AccountScreenState.READ)
    val accountScreenState = _accountScreenState.asStateFlow()
    val user: Flow<User?> = getCurrentUserUseCase()
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
                updateUserProfilePictureUseCase(uri)
                    .onSuccess {
                        _accountScreenState.value =
                            AccountScreenState.PROFILE_PICTURE_UPDATED
                    }
                    .onFailure {
                        _accountScreenState.value =
                            AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR
                    }
                resetProfilePictureUri()
            }
        }
    }

    fun deleteUserProfilePicture() {
        _accountScreenState.value = AccountScreenState.LOADING

        viewModelScope.launch {
            val (id, profilePictureUrl) = user.first()?.id to user.first()?.profilePictureUrl
            deleteUserProfilePictureUseCase(id!!, profilePictureUrl!!)
                .onSuccess {
                    _accountScreenState.value = AccountScreenState.PROFILE_PICTURE_UPDATED
                }
                .onFailure {
                    _accountScreenState.value =
                        AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR
                }
            resetProfilePictureUri()
        }
    }
}