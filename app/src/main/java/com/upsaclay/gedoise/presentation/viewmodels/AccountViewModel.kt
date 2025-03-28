package com.upsaclay.gedoise.presentation.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.DeleteProfilePictureUseCase
import com.upsaclay.common.domain.usecase.UpdateProfilePictureUseCase
import com.upsaclay.gedoise.domain.entities.AccountErrorType
import com.upsaclay.gedoise.domain.entities.AccountEvent
import com.upsaclay.gedoise.domain.entities.AccountScreenState
import com.upsaclay.news.domain.entity.AnnouncementEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val deleteProfilePictureUseCase: DeleteProfilePictureUseCase,
    userRepository: UserRepository
): ViewModel() {
    private val _screenState = MutableStateFlow(AccountScreenState.READ)
    val screenState: StateFlow<AccountScreenState> = _screenState
    private val _event = MutableSharedFlow<AccountEvent>()
    val event: SharedFlow<AccountEvent> = _event
    val currentUser: StateFlow<User?> = userRepository.currentUser
    var profilePictureUri by mutableStateOf<Uri?>(null)
        private set

    fun updateUserProfilePicture() {
        profilePictureUri?.let { uri ->
            viewModelScope.launch {
                _event.emit(AccountEvent.Loading)
                try {
                    updateProfilePictureUseCase(uri)
                    _screenState.value = AccountScreenState.READ
                } catch (e: Exception) {
                    _event.emit(AccountEvent.Error(AccountErrorType.PROFILE_PICTURE_UPDATE_ERROR))
                }
            }
        }
    }

    fun deleteUserProfilePicture() {
        viewModelScope.launch {
            _event.emit(AccountEvent.Loading)
            val (id, url) = currentUser.value?.id to currentUser.value?.profilePictureUrl
            try {
                deleteProfilePictureUseCase(id!!, url!!)
                resetProfilePictureUri()
                _event.emit(AccountEvent.ProfilePictureDeleted)
                _screenState.value = AccountScreenState.READ
            } catch (e: Exception) {
                _event.emit(AccountEvent.Error(AccountErrorType.PROFILE_PICTURE_UPDATE_ERROR))
            }
        }
    }

    fun updateScreenState(screenState: AccountScreenState) {
        _screenState.value = screenState
    }

    fun resetScreenState() {
        _screenState.value = AccountScreenState.READ
    }

    fun updateProfilePictureUri(uri: Uri) {
        profilePictureUri = uri
    }

    fun resetProfilePictureUri() {
        profilePictureUri = null
    }
}