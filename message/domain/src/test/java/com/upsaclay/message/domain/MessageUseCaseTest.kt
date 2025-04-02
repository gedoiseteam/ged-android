package com.upsaclay.message.domain

import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetNewConversationMessageUseCase
import com.upsaclay.message.domain.usecase.GetPagedConversationsUIUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteMessagesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
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
    private lateinit var getNewConversationMessageUseCase: GetNewConversationMessageUseCase
    private lateinit var getPagedConversationsUIUseCase: GetPagedConversationsUIUseCase
    private lateinit var listenRemoteConversationsUseCase: ListenRemoteConversationsUseCase
    private lateinit var listenRemoteMessagesUseCase: ListenRemoteMessagesUseCase

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        getPagedConversationsUIUseCase = GetPagedConversationsUIUseCase(userConversationRepository = userConversationRepository)
        createConversationUseCase = CreateConversationUseCase(userConversationRepository = userConversationRepository)
        getNewConversationMessageUseCase = GetNewConversationMessageUseCase(
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository
        )
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

        coEvery { userConversationRepository.conversationsWithLastMessage } returns flowOf(conversationsMessageFixture)
        coEvery { userConversationRepository.getConversations() } returns flowOf(conversationsFixture)
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
        createConversationUseCase(conversationFixture)

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
        coVerify { userConversationRepository.deleteConversation(ConversationMapper.toConversation(conversation)) }
        coVerify { messageRepository.deleteMessages(conversation.id) }
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
    fun getNewConversationMessageUseCase_should_get_new_conversation_messages() = runTest {
        // Given
        val conversationMessage = conversationMessageFixture.copy(
            lastMessage = conversationMessageFixture.lastMessage!!.copy(seen = null)
        )
        coEvery { userConversationRepository.getConversations() } returns flowOf(
            listOf(conversationMessage.conversation)
        )
        coEvery { messageRepository.getRemoteMessages(any()) } returns flowOf(
            listOf(conversationMessage.lastMessage!!)
        )

        // When
        val result = getNewConversationMessageUseCase()

        // Then
        assertEquals(listOf(conversationMessage), result.toList().get(0))
        coVerify { messageRepository.getRemoteMessages(conversationMessage.conversation.id) }
    }
}