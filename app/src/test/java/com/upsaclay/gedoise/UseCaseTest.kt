package com.upsaclay.gedoise

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.gedoise.domain.usecase.ClearDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenMessagesUseCase
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
    private val listenConversationsUiUseCase: ListenConversationsUiUseCase = mockk()
    private val listenMessagesUseCase: ListenMessagesUseCase = mockk()
    private val listenConversationsUseCase: ListenConversationsUseCase = mockk()

    private lateinit var clearDataUseCase: ClearDataUseCase
    private lateinit var startListeningDataUseCase: StartListeningDataUseCase
    private lateinit var stopListeningDataUseCase: StopListeningDataUseCase

    @Before
    fun setUp() {
        every { listenConversationsUiUseCase.start() } returns Unit
        every { listenConversationsUiUseCase.stop() } returns Unit
        every { listenConversationsUiUseCase.clearCache() } returns Unit
        every { listenConversationsUseCase.start() } returns Unit
        every { listenConversationsUseCase.stop() } returns Unit
        every { listenMessagesUseCase.start() } returns Unit
        every { listenMessagesUseCase.stop() } returns Unit
        coEvery { userRepository.deleteCurrentUser() } returns Unit
        coEvery { userConversationRepository.deleteLocalConversations() } returns Unit
        coEvery { messageRepository.deleteLocalMessages() } returns Unit


        clearDataUseCase = ClearDataUseCase(
            userRepository = userRepository,
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository,
            listenConversationsUiUseCase = listenConversationsUiUseCase
        )

        startListeningDataUseCase = StartListeningDataUseCase(
            listenConversationsUiUseCase = listenConversationsUiUseCase,
            listenMessagesUseCase = listenMessagesUseCase,
            listenConversationsUseCase = listenConversationsUseCase
        )

        stopListeningDataUseCase = StopListeningDataUseCase(
            listenConversationsUiUseCase = listenConversationsUiUseCase,
            listenMessagesUseCase = listenMessagesUseCase,
            listenConversationsUseCase = listenConversationsUseCase
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
        every { listenConversationsUiUseCase.clearCache() }
    }

    @Test
    fun startListeningDataUseCase_should_start_listening_data() = runTest {
        // When
        startListeningDataUseCase()

        // Then
        every { listenConversationsUiUseCase.start() }
        every { listenConversationsUseCase.start() }
        every { listenMessagesUseCase.start() }
    }

    @Test
    fun stopListeningDataUseCase_should_stop_listening_data() = runTest {
        // When
        stopListeningDataUseCase()

        // Then
        every { listenConversationsUiUseCase.stop() }
        every { listenConversationsUseCase.stop() }
        every { listenMessagesUseCase.stop() }
    }
}