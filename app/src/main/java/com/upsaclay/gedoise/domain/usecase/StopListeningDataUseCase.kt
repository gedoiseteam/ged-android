package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenMessagesUseCase

class StopListeningDataUseCase(
    private val listenConversationsUseCase: ListenConversationsUseCase,
    private val listenMessagesUseCase: ListenMessagesUseCase,
    private val listenConversationsUiUseCase: ListenConversationsUiUseCase
) {
    operator fun invoke() {
        listenConversationsUseCase.stop()
        listenMessagesUseCase.stop()
        listenConversationsUiUseCase.stop()
    }
}