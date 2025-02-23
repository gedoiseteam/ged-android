package com.upsaclay.message.domain

import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class MessageUseCaseTest {
    private val messageRepository: MessageRepository = mockk()
    private val userConversationRepository: UserConversationRepository = mockk()
    private val listenConversationsUiUseCaseMockk: ListenConversationsUiUseCase = mockk()

    private lateinit var createConversationUseCase: CreateConversationUseCase
    private lateinit var deleteConversationUseCase: DeleteConversationUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var listenConversationsUiUseCase: ListenConversationsUiUseCase
    private lateinit var listenConversationsUseCase: ListenConversationsUseCase
    private lateinit var listenMessagesUseCase: ListenMessagesUseCase

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        listenConversationsUiUseCase = ListenConversationsUiUseCase(
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository,
            scope = testScope
        )
        createConversationUseCase = CreateConversationUseCase(
            userConversationRepository = userConversationRepository,
            scope = testScope
        )
        deleteConversationUseCase = DeleteConversationUseCase(
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository,
            listenConversationsUiUseCase = listenConversationsUiUseCaseMockk,
            scope = testScope
        )
        sendMessageUseCase = SendMessageUseCase(
            messageRepository = messageRepository,
            scope = testScope
        )
        listenConversationsUseCase = ListenConversationsUseCase(
            userConversationRepository = userConversationRepository,
            scope = testScope
        )
        listenMessagesUseCase = ListenMessagesUseCase(
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository,
            scope = testScope
        )

        every { listenConversationsUiUseCaseMockk.deleteConversation(any()) } returns Unit
        every { userConversationRepository.userConversations } returns MutableStateFlow(conversationUserFixture)
        every { messageRepository.getMessages(any()) } returns MutableStateFlow(messageFixture)
        every { messageRepository.getLastMessage(any()) } returns MutableStateFlow(messageFixture)
        coEvery { userConversationRepository.userConversations } returns flowOf(conversationUserFixture)
        coEvery { userConversationRepository.createConversation(any()) } returns Unit
        coEvery { userConversationRepository.updateConversation(any()) } returns Unit
        coEvery { userConversationRepository.deleteConversation(any()) } returns Unit
        coEvery { userConversationRepository.deleteLocalConversations() } returns Unit
        coEvery { userConversationRepository.listenRemoteConversations() } returns Unit
        coEvery { userConversationRepository.listenLocalConversations() } returns Unit
        coEvery { messageRepository.getLastMessage(any()) } returns flowOf(messageFixture)
        coEvery { messageRepository.createMessage(any()) } returns Unit
        coEvery { messageRepository.updateMessage(any()) } returns Unit
        coEvery { messageRepository.upsertMessage(any()) } returns Unit
        coEvery { messageRepository.deleteLocalMessages() } returns Unit
    }

    @Test
    fun createConversationUseCase_should_create_conversation() = runTest {
        // When
        createConversationUseCase(conversationUIFixture)

        // Then
        coVerify { userConversationRepository.createConversation(conversationUserFixture) }
    }

    @Test
    fun deleteConversationUseCase_should_delete_conversation() = runTest {
        // Given
        val conversation = conversationUIFixture

        // When
        deleteConversationUseCase(conversation)

        // Then
        coVerify { userConversationRepository.deleteConversation(ConversationMapper.toConversationUser(conversation)) }
        verify { listenConversationsUiUseCaseMockk.deleteConversation(conversation) }
        coVerify { messageRepository.deleteMessages(conversation.id) }
    }

    @Test
    fun sendMessageUseCase_should_create_message() = runTest {
        // When
        sendMessageUseCase(messageFixture)

        // Then
        coVerify { messageRepository.createMessage(messageFixture) }
    }

    @Test
    fun listenConversationsUIUseCase_should_listen_conversations_ui() = runTest {
        // Given
        val conversationExpected = ConversationMapper.toConversationUI(conversationUserFixture, messageFixture)
        val expectedResult = listOf(conversationExpected)

        // When
        listenConversationsUiUseCase.start()
        val result = listenConversationsUiUseCase.currentConversationsUI

        // Then
        assert(listenConversationsUiUseCase.job != null)
        coVerify { userConversationRepository.userConversations }
        coVerify { messageRepository.getLastMessage(conversationUserFixture.id) }
        assertEquals(expectedResult, result)
    }

    @Test
    fun listenConversationsUIUseCase_should_stop_conversations_listening() = runTest {
        // Given
        listenConversationsUiUseCase.start()

        // When
        listenConversationsUiUseCase.stop()

        // Then
        assertFalse(listenConversationsUiUseCase.job!!.isActive)
    }

    @Test
    fun listenConversationsUseCase_should_start_conversations_listening() = runTest {
        // When
        listenConversationsUseCase.start()

        // Then
        assert(listenConversationsUseCase.job != null)
        coVerify { userConversationRepository.listenLocalConversations() }
        coVerify { userConversationRepository.listenRemoteConversations() }
    }

    @Test
    fun listenConversationsUseCase_should_stop_conversations_listening() = runTest {
        // Given
        listenConversationsUseCase.start()

        // When
        listenConversationsUseCase.stop()

        // Then
        assertFalse(listenConversationsUseCase.job!!.isCancelled)
    }

    @Test
    fun listenMessagesUseCase_should_start_messages_listening() = runTest {
        // When
        listenMessagesUseCase.start()

        // Then
        coVerify { messageRepository.listenRemoteMessages(conversationUserFixture.id) }
    }

    @Test
    fun listenMessagesUseCase_should_stop_messages_listening() = runTest {
        // Given
        listenMessagesUseCase.start()

        // When
        listenMessagesUseCase.stop()

        // Then
        assert(listenMessagesUseCase.job!!.isCancelled)
    }
}