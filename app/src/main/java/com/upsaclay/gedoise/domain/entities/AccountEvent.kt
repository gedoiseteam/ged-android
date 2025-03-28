package com.upsaclay.gedoise.domain.entities

import com.upsaclay.common.domain.entity.ErrorType

sealed class AccountEvent {
    data object ProfilePictureUpdated: AccountEvent()
    data object ProfilePictureDeleted: AccountEvent()
    data class Error(val type: ErrorType): AccountEvent()
    data object Loading: AccountEvent()
}

enum class AccountErrorType: ErrorType {
    PROFILE_PICTURE_UPDATE_ERROR
}