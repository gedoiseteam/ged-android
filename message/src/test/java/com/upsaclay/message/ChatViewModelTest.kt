package com.upsaclay.message

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.NotificationUseCase
import com.upsaclay.common.domain.userFixture
import com.upsaclay.message.domain.conversationFixture
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.messagesFixture
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val createConversationUseCase: CreateConversationUseCase = mockk()
    private val userRepository: UserRepository = mockk()
    private val messageRepository: MessageRepository = mockk()
    private val sendMessageUseCase: SendMessageUseCase = mockk()
    private val notificationUseCase: NotificationUseCase = mockk()

    private lateinit var chatViewModel: ChatViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { userRepository.currentUser } returns MutableStateFlow(userFixture)
        every { messageRepository.getMessages(any()) } returns flowOf(messagesFixture)
        coEvery { messageRepository.addMessage(any()) } returns Unit
        coEvery { createConversationUseCase(any()) } returns Unit

        chatViewModel = ChatViewModel(
            conversation = conversationFixture,
            userRepository = userRepository,
            messageRepository = messageRepository,
            createConversationUseCase = createConversationUseCase,
            sendMessageUseCase = sendMessageUseCase,
            notificationUseCase = notificationUseCase,

        )
    }

    @Test
    fun default_values_are_correct() = runTest {
        // Then
        assertEquals("", chatViewModel.text.value)
    }

    @Test
    fun update_text_to_send_should_update_text_to_send() {
        // Given
        val text = "Hello"

        // When
        chatViewModel.updateTextToSend(text)

        // Then
        assertEquals(text, chatViewModel.text.value)
    }

    @Test
    fun send_message_should_send_message() {
        // Given
        chatViewModel.updateTextToSend("Hello")

        // When
        chatViewModel.sendMessage()

        // Then
        coVerify { messageRepository.addMessage(any()) }
    }

    @Test
    fun send_message_should_not_send_message_when_text_is_empty() {
        // When
        chatViewModel.sendMessage()

        // Then
        coVerify(exactly = 0) { messageRepository.addMessage(any()) }
    }

    @Test
    fun send_message_should_reset_text_to_send() {
        // Given
        chatViewModel.updateTextToSend("Hello")

        // When
        chatViewModel.sendMessage()

        // Then
        assertEquals("", chatViewModel.text.value)
    }

    @Test
    fun send_message_should_create_conversation_when_it_is_not_created() {
        // Given
        val conversation = conversationFixture.copy(state = ConversationState.NOT_CREATED)
        chatViewModel = ChatViewModel(
            conversation = conversationFixture,
            userRepository = userRepository,
            messageRepository = messageRepository,
            createConversationUseCase = createConversationUseCase,
            sendMessageUseCase = sendMessageUseCase,
            notificationUseCase = notificationUseCase
        )
        chatViewModel.updateTextToSend("Hello")

        // When
        chatViewModel.sendMessage()

        // Then
        coVerify { createConversationUseCase(conversation) }
    }

    @Test
    fun send_message_should_not_create_conversation_when_it_is_created() {
        // Given
        val conversation = conversationFixture.copy(state = ConversationState.CREATED)
        chatViewModel = ChatViewModel(
            conversation = conversationFixture,
            userRepository = userRepository,
            messageRepository = messageRepository,
            createConversationUseCase = createConversationUseCase,
            sendMessageUseCase = sendMessageUseCase,
            notificationUseCase = notificationUseCase
        )
        chatViewModel.updateTextToSend("Hello")

        // When
        chatViewModel.sendMessage()

        // Then
        coVerify(exactly = 0) { createConversationUseCase(conversation) }
        coVerify { messageRepository.addMessage(any()) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun send_message_should_throw_exception_when_current_user_is_null() {
        // Given
        every { userRepository.currentUser } returns MutableStateFlow(null)
        chatViewModel = ChatViewModel(
            conversation = conversationFixture,
            userRepository = userRepository,
            messageRepository = messageRepository,
            createConversationUseCase = createConversationUseCase,
            sendMessageUseCase = sendMessageUseCase,
            notificationUseCase = notificationUseCase
        )
        chatViewModel.updateTextToSend("Hello")

        // When
        chatViewModel.sendMessage()
    }
}