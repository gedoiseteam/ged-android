package com.upsaclay.message.domain

import com.upsaclay.common.domain.userFixture
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUIUseCase
import com.upsaclay.message.domain.usecase.GetConversationUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private lateinit var createConversationUseCase: CreateConversationUseCase
    private lateinit var deleteConversationUseCase: DeleteConversationUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var getConversationUIUseCase: GetConversationUIUseCase
    private lateinit var getConversationUseCase: GetConversationUseCase
    private lateinit var listenRemoteConversationsUseCase: ListenRemoteConversationsUseCase
    private lateinit var listenRemoteMessagesUseCase: ListenRemoteMessagesUseCase

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        getConversationUIUseCase = GetConversationUIUseCase(userConversationRepository = userConversationRepository)
        getConversationUseCase = GetConversationUseCase(userConversationRepository = userConversationRepository)
        createConversationUseCase = CreateConversationUseCase(userConversationRepository = userConversationRepository)
        sendMessageUseCase = SendMessageUseCase(messageRepository = messageRepository)
        deleteConversationUseCase = DeleteConversationUseCase(
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository
        )
        listenRemoteConversationsUseCase = ListenRemoteConversationsUseCase(
            userConversationRepository = userConversationRepository,
            scope = testScope
        )
        listenRemoteMessagesUseCase = ListenRemoteMessagesUseCase(
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository,
            scope = testScope
        )

        coEvery { userConversationRepository.conversations } returns flowOf(conversationsFixture)
        coEvery { userConversationRepository.getConversation(any()) } returns conversationFixture
        coEvery { userConversationRepository.createConversation(any()) } returns Unit
        coEvery { userConversationRepository.updateConversation(any()) } returns Unit
        coEvery { userConversationRepository.deleteConversation(any()) } returns Unit
        coEvery { userConversationRepository.deleteLocalConversations() } returns Unit
        coEvery { userConversationRepository.listenRemoteConversations() } returns Unit
        coEvery { messageRepository.listenRemoteMessages(any()) } returns Unit
        coEvery { messageRepository.createMessage(any()) } returns Unit
        coEvery { messageRepository.updateMessage(any()) } returns Unit
        coEvery { messageRepository.upsertMessage(any()) } returns Unit
        coEvery { messageRepository.deleteLocalMessages() } returns Unit
        coEvery { messageRepository.deleteMessages(any()) } returns Unit
    }

    @Test
    fun createConversationUseCase_should_create_conversation() = runTest {
        // When
        createConversationUseCase(conversationUIFixture)

        // Then
        coVerify { userConversationRepository.createConversation(conversationFixture) }
    }

    @Test
    fun deleteConversationUseCase_should_delete_conversation() = runTest {
        // Given
        val conversation = conversationUIFixture

        // When
        deleteConversationUseCase(conversation)

        // Then
        coVerify { userConversationRepository.deleteConversation(ConversationMapper.toConversationUser(conversation)) }
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
    fun listenConversationsUseCase_should_start_remote_conversations_listening() = runTest {
        // When
        listenRemoteConversationsUseCase.start()

        // Then
        assert(listenRemoteConversationsUseCase.job != null)
        coVerify { userConversationRepository.listenRemoteConversations() }
    }

    @Test
    fun listenConversationsUseCase_should_stop_conversations_listening() = runTest {
        // Given
        listenRemoteConversationsUseCase.start()

        // When
        listenRemoteConversationsUseCase.stop()

        // Then
        assertFalse(listenRemoteConversationsUseCase.job!!.isCancelled)
    }

    @Test
    fun listenRemoteMessagesUseCase_should_start_remote_messages_listening() = runTest {
        // When
        listenRemoteMessagesUseCase.start()

        // Then
        coVerify { messageRepository.listenRemoteMessages(conversationFixture.id) }
    }

    @Test
    fun listenRemoteMessagesUseCase_should_stop_remote_messages_listening() = runTest {
        // Given
        listenRemoteMessagesUseCase.start()

        // When
        listenRemoteMessagesUseCase.stop()

        // Then
        assertFalse(listenRemoteMessagesUseCase.job!!.isActive)
    }

    @Test
    fun getConversationUseCase_should_return_conversation() = runTest {
        // Given
        coEvery { userConversationRepository.getConversation(any()) } returns conversationFixture

        // When
        val result = getConversationUseCase(userFixture.id)

        // Then
        assertEquals(result, conversationFixture)
    }
}