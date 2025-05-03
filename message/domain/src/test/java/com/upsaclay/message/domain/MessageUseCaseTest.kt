package com.upsaclay.message.domain

import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.ConversationRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteMessagesUseCase
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
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class MessageUseCaseTest {
    private val messageRepository: MessageRepository = mockk()
    private val conversationRepository: ConversationRepository = mockk()

    private lateinit var createConversationUseCase: CreateConversationUseCase
    private lateinit var deleteConversationUseCase: DeleteConversationUseCase
    private lateinit var listenRemoteConversationsUseCase: ListenRemoteConversationsUseCase
    private lateinit var listenRemoteMessagesUseCase: ListenRemoteMessagesUseCase

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        createConversationUseCase = CreateConversationUseCase(conversationRepository = conversationRepository)
        deleteConversationUseCase = DeleteConversationUseCase(
            conversationRepository = conversationRepository,
            messageRepository = messageRepository
        )
        listenRemoteConversationsUseCase = ListenRemoteConversationsUseCase(
            conversationRepository = conversationRepository,
            scope = testScope
        )
        listenRemoteMessagesUseCase = ListenRemoteMessagesUseCase(
            conversationRepository = conversationRepository,
            messageRepository = messageRepository,
            scope = testScope
        )

        coEvery { conversationRepository.conversationsMessage } returns flowOf(conversationsMessageFixture)
        coEvery { conversationRepository.conversations } returns flowOf(conversationsFixture)
        coEvery { conversationRepository.getConversationFromLocal(any()) } returns conversationFixture
        coEvery { conversationRepository.createConversation(any()) } returns Unit
        coEvery { conversationRepository.deleteConversation(any()) } returns Unit
        coEvery { conversationRepository.deleteLocalConversations() } returns Unit
        coEvery { conversationRepository.getRemoteConversations() } returns Unit
        coEvery { messageRepository.listenRemoteMessages(any()) } returns Unit
        coEvery { messageRepository.addMessage(any()) } returns Unit
        coEvery { messageRepository.updateMessage(any()) } returns Unit
        coEvery { messageRepository.upsertMessage(any()) } returns Unit
        coEvery { messageRepository.deleteLocalMessages() } returns Unit
        coEvery { messageRepository.deleteMessages(any()) } returns Unit
    }

    @Test
    fun createConversationUseCase_should_create_conversation() = runTest {
        // When
        createConversationUseCase(conversationFixture)

        // Then
        coVerify { conversationRepository.createConversation(conversationFixture) }
    }

    @Test
    fun deleteConversationUseCase_should_delete_conversation() = runTest {
        // Given
        val conversation = conversationUIFixture

        // When
        deleteConversationUseCase(conversation)

        // Then
        coVerify { conversationRepository.deleteConversation(ConversationMapper.toConversation(conversation)) }
        coVerify { messageRepository.deleteMessages(conversation.id) }
    }

    @Test
    fun listenConversationsUseCase_should_start_remote_conversations_listening() = runTest {
        // When
        listenRemoteConversationsUseCase.start()

        // Then
        assert(listenRemoteConversationsUseCase.job != null)
        coVerify { conversationRepository.getRemoteConversations() }
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
}