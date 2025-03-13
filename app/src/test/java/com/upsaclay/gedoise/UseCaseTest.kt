package com.upsaclay.gedoise

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.gedoise.domain.usecase.ClearDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.ListenRemoteConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteMessagesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val userConversationRepository: UserConversationRepository = mockk()
    private val messageRepository: MessageRepository = mockk()
    private val listenRemoteMessagesUseCase: ListenRemoteMessagesUseCase = mockk()
    private val listenRemoteConversationsUseCase: ListenRemoteConversationsUseCase = mockk()

    private lateinit var clearDataUseCase: ClearDataUseCase
    private lateinit var startListeningDataUseCase: StartListeningDataUseCase
    private lateinit var stopListeningDataUseCase: StopListeningDataUseCase

    @Before
    fun setUp() {
        every { listenRemoteConversationsUseCase.start() } returns Unit
        every { listenRemoteConversationsUseCase.stop() } returns Unit
        every { listenRemoteMessagesUseCase.start() } returns Unit
        every { listenRemoteMessagesUseCase.stop() } returns Unit
        coEvery { userRepository.deleteCurrentUser() } returns Unit
        coEvery { userConversationRepository.deleteLocalConversations() } returns Unit
        coEvery { messageRepository.deleteLocalMessages() } returns Unit


        clearDataUseCase = ClearDataUseCase(
            userRepository = userRepository,
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository
        )

        startListeningDataUseCase = StartListeningDataUseCase(
            listenRemoteMessagesUseCase = listenRemoteMessagesUseCase,
            listenRemoteConversationsUseCase = listenRemoteConversationsUseCase
        )

        stopListeningDataUseCase = StopListeningDataUseCase(
            listenRemoteMessagesUseCase = listenRemoteMessagesUseCase,
            listenRemoteConversationsUseCase = listenRemoteConversationsUseCase
        )
    }

    @Test
    fun clearDataUseCase_should_delete_all_data() = runTest {
        // When
        clearDataUseCase()

        // Then
        coVerify { userRepository.deleteCurrentUser() }
        coVerify { userConversationRepository.deleteLocalConversations() }
        coVerify { messageRepository.deleteLocalMessages() }
    }

    @Test
    fun startListeningDataUseCase_should_start_listening_data() = runTest {
        // When
        startListeningDataUseCase()

        // Then
        every { listenRemoteConversationsUseCase.start() }
        every { listenRemoteMessagesUseCase.start() }
    }

    @Test
    fun stopListeningDataUseCase_should_stop_listening_data() = runTest {
        // When
        stopListeningDataUseCase()

        // Then
        every { listenRemoteConversationsUseCase.stop() }
        every { listenRemoteMessagesUseCase.stop() }
    }
}