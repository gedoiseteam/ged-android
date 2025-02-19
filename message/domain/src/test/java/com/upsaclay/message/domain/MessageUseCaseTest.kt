package com.upsaclay.message.domain

import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUserUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    private lateinit var getConversationUserUseCase: GetConversationUserUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var listenConversationsUiUseCase: ListenConversationsUiUseCase
    private lateinit var listenConversationsUseCase: ListenConversationsUseCase
    private lateinit var listenMessagesUseCase: ListenMessagesUseCase

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        createConversationUseCase = CreateConversationUseCase(userConversationRepository = userConversationRepository)
        deleteConversationUseCase = DeleteConversationUseCase(userConversationRepository = userConversationRepository)
        getConversationUserUseCase = GetConversationUserUseCase(userConversationRepository = userConversationRepository)
        sendMessageUseCase = SendMessageUseCase(messageRepository = messageRepository)
        listenConversationsUiUseCase = ListenConversationsUiUseCase(
            userConversationRepository = userConversationRepository,
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

        every { userConversationRepository.userConversations } returns MutableStateFlow(conversationUserFixture)
        every { userConversationRepository.getUserConversation(any()) } returns conversationUserFixture
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
    fun getConversationUserUseCase_should_return_conversation_with_user() = runTest {
        // When
        val result = getConversationUserUseCase(conversationUIFixture.id)

        // Then
        assertEquals(conversationUserFixture, result)
    }

    @Test
    fun createConversationUseCase_should_create_conversation() = runTest {
        // When
        createConversationUseCase(conversationUserFixture)

        // Then
        coVerify { userConversationRepository.createConversation(conversationUserFixture) }
    }

    @Test
    fun deleteConversationUseCase_should_delete_conversation() = runTest {
        // When
        deleteConversationUseCase(conversationUIFixture)

        // Then
        coVerify { userConversationRepository.deleteConversation(conversationUserFixture) }
    }

    @Test
    fun sendMessageUseCase_should_create_message() = runTest {
        // When
        sendMessageUseCase(messageFixture)

        // Then
        coVerify { messageRepository.createMessage(messageFixture) }
        coVerify { messageRepository.updateMessage(messageFixture.copy(state = MessageState.SENT)) }
    }

    @Test
    fun sendMessageUseCase_should_update_message_state_to_error_when_exception_is_throwing() = runTest {
        // Given
        coEvery { messageRepository.createMessage(messageFixture) } throws Exception()

        // When
        sendMessageUseCase(messageFixture)

        // Then
        coVerify { messageRepository.upsertMessage(messageFixture.copy(state = MessageState.ERROR)) }
    }

    @Test
    fun listenConversationsUIUseCase_should_listen_conversations_ui() = runTest {
        // Given
        val conversationExpected = ConversationMapper.toConversationUI(conversationUserFixture, messageFixture)
        val expectedResult = listOf(conversationExpected)

        // When
        listenConversationsUiUseCase.start()
        val result = listenConversationsUiUseCase.conversationsUI.first()

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