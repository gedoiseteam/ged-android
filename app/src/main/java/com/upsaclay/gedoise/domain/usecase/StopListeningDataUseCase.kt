package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.message.domain.usecase.ListenRemoteConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteMessagesUseCase

class StopListeningDataUseCase(
    private val listenRemoteConversationsUseCase: ListenRemoteConversationsUseCase,
    private val listenRemoteMessagesUseCase: ListenRemoteMessagesUseCase,
) {
    operator fun invoke() {
        listenRemoteConversationsUseCase.stop()
        listenRemoteMessagesUseCase.stop()
    }
}