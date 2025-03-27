package com.upsaclay.message.domain.entity

import com.upsaclay.common.domain.entity.ErrorType

sealed class ConversationEvent {
    data object Loading: ConversationEvent()
    data class Success(val type: SuccessType): ConversationEvent()
    data class Error(val type: ErrorType): ConversationEvent()
}

enum class SuccessType {
    LOADED,
    DELETED
}