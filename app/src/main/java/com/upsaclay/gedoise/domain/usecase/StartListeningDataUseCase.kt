package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenMessagesUseCase

class StartListeningDataUseCase(
    private val listenConversationsUseCase: ListenConversationsUseCase,
    private val listenMessagesUseCase: ListenMessagesUseCase,
    private val listenConversationsUiUseCase: ListenConversationsUiUseCase
) {
    operator fun invoke() {
        listenConversationsUseCase.start()
        listenMessagesUseCase.start()
        listenConversationsUiUseCase.start()
    }
}