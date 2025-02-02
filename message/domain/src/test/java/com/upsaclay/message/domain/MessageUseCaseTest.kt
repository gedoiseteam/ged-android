package com.upsaclay.message.domain

import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUserUseCase
import com.upsaclay.message.domain.usecase.GetConversationsUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class MessageUseCaseTest {
    private val messageRepository: MessageRepository = mockk()
    private val userConversationRepository: UserConversationRepository = mockk()

    private lateinit var createConversationUseCase: CreateConversationUseCase
    private lateinit var deleteConversationUseCase: DeleteConversationUseCase
    private lateinit var getConversationsUseCase: GetConversationsUseCase
    private lateinit var getConversationUserUseCase: GetConversationUserUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase

    @Before
    fun setUp() {
        createConversationUseCase = CreateConversationUseCase(userConversationRepository)
        deleteConversationUseCase = DeleteConversationUseCase(userConversationRepository)
        getConversationsUseCase = GetConversationsUseCase(userConversationRepository, messageRepository)
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
    }

    @Test
    fun `getConversationsUseCase should return conversations`() = runTest {
        // When
        val result = getConversationsUseCase().first()

        // Then
        assertEquals(conversationFixture, result)
    }

    @Test
    fun `getConversationUserUseCase should return conversation with user`() = runTest {
        // When
        val result = getConversationUserUseCase(conversationFixture.id)

        // Then
        assertEquals(conversationUserFixture, result)
    }

    @Test
    fun `createConversationUseCase should create conversation`() = runTest {
        // When
        createConversationUseCase(conversationFixture)

        // Then
        coVerify { userConversationRepository.createConversation(conversationUserFixture) }
    }

    @Test
    fun `deleteConversationUseCase should delete conversation`() = runTest {
        // When
        deleteConversationUseCase(conversationFixture)

        // Then
        coVerify { userConversationRepository.deleteConversation(conversationUserFixture) }
    }

    @Test
    fun `sendMessageUseCase should create message`() = runTest {
        // When
        sendMessageUseCase(messageFixture)

        // Then
        coVerify { messageRepository.createMessage(messageFixture) }
        coVerify { messageRepository.updateMessage(messageFixture.copy(state = MessageState.SENT)) }
    }

    @Test
    fun `sendMessageUseCase should update message state to error when exception is throwing`() = runTest {
        // Given
        coEvery { messageRepository.createMessage(messageFixture) } throws Exception()

        // When
        sendMessageUseCase(messageFixture)

        // Then
        coVerify { messageRepository.upsertMessage(messageFixture.copy(state = MessageState.ERROR)) }
    }
}