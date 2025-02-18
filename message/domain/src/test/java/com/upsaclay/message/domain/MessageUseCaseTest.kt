package com.upsaclay.message.domain

import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUserUseCase
import com.upsaclay.message.domain.usecase.GetConversationsUIUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MessageUseCaseTest {
    private val messageRepository: MessageRepository = mockk()
    private val userConversationRepository: UserConversationRepository = mockk()

    private lateinit var createConversationUseCase: CreateConversationUseCase
    private lateinit var deleteConversationUseCase: DeleteConversationUseCase
    private lateinit var getConversationsUIUseCase: GetConversationsUIUseCase
    private lateinit var getConversationUserUseCase: GetConversationUserUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        createConversationUseCase = CreateConversationUseCase(userConversationRepository)
        deleteConversationUseCase = DeleteConversationUseCase(userConversationRepository)
        getConversationUserUseCase = GetConversationUserUseCase(userConversationRepository)
        sendMessageUseCase = SendMessageUseCase(messageRepository)

        every { userConversationRepository.userConversations } returns MutableStateFlow(conversationUserFixture)
        every { userConversationRepository.getUserConversation(any()) } returns conversationUserFixture
        every { userConversationRepository.stopListenConversations() } returns Unit
        every { userConversationRepository.listenConversations() } returns Unit
        every { messageRepository.getMessages(any()) } returns MutableStateFlow(messageFixture)
        every { messageRepository.getLastMessage(any()) } returns MutableStateFlow(messageFixture)
        every { messageRepository.stopListenMessages() } returns Unit

        coEvery { userConversationRepository.createConversation(any()) } returns Unit
        coEvery { userConversationRepository.updateConversation(any()) } returns Unit
        coEvery { userConversationRepository.deleteConversation(any()) } returns Unit
        coEvery { userConversationRepository.deleteLocalConversations() } returns Unit
        coEvery { messageRepository.createMessage(any()) } returns Unit
        coEvery { messageRepository.updateMessage(any()) } returns Unit
        coEvery { messageRepository.upsertMessage(any()) } returns Unit
        coEvery { messageRepository.deleteLocalMessages() } returns Unit

        getConversationsUIUseCase = GetConversationsUIUseCase(
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository,
            scope = testScope
        )
    }

    @Test
    fun getConversationsUIUseCase_should_return_conversationsUI() = runTest {
        // When
        val result = getConversationsUIUseCase().first()

        // Then
        assertEquals(listOf(conversationUIFixture), result)
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
        createConversationUseCase(conversationUIFixture)

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
//        coVerify { messageRepository.updateMessage(messageFixture.copy(state = MessageState.SENT)) }
    }

    @Test
    fun sendMessageUseCase_should_update_message_state_to_error_when_exception_is_throwing() = runTest {
        // Given
        coEvery { messageRepository.createMessage(messageFixture) } throws Exception()

        // When
        sendMessageUseCase(messageFixture)

        // Then
//        coVerify { messageRepository.upsertMessage(messageFixture.copy(state = MessageState.ERROR)) }
    }
}