package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.message.domain.usecase.ListenRemoteConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteMessagesUseCase

class StartListeningDataUseCase(
    private val listenRemoteConversationsUseCase: ListenRemoteConversationsUseCase,
    private val listenRemoteMessagesUseCase: ListenRemoteMessagesUseCase
) {
    operator fun invoke() {
        listenRemoteConversationsUseCase.start()
        listenRemoteMessagesUseCase.start()
    }
}