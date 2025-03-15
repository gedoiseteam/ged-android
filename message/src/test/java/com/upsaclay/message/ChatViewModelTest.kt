package com.upsaclay.message

import androidx.paging.PagingData
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.userFixture
import com.upsaclay.message.domain.conversationUIFixture
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.messageFixture
import com.upsaclay.message.domain.messagesFixture
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.GetLastMessageUseCase
import com.upsaclay.message.domain.usecase.GetMessagesUseCase
import com.upsaclay.message.domain.usecase.GetUnreadMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import com.upsaclay.message.domain.usecase.UpdateMessageUseCase
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val getMessagesUseCase: GetMessagesUseCase = mockk()
    private val getLastMessageUseCase: GetLastMessageUseCase = mockk()
    private val sendMessageUseCase: SendMessageUseCase = mockk()
    private val createConversationUseCase: CreateConversationUseCase = mockk()
    private val updateMessageUseCase: UpdateMessageUseCase = mockk()
    private val getUnreadMessagesUseCase: GetUnreadMessagesUseCase = mockk()

    private lateinit var chatViewModel: ChatViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getCurrentUserUseCase() } returns MutableStateFlow(userFixture)
        every { getMessagesUseCase(any()) } returns flowOf(PagingData.from(messagesFixture))
        every { getLastMessageUseCase(any()) } returns flowOf(messageFixture)
        coEvery { sendMessageUseCase(any()) } returns Unit
        coEvery { createConversationUseCase(any()) } returns Unit

        chatViewModel = ChatViewModel(
            conversation = conversationUIFixture,
            getCurrentUserUseCase = getCurrentUserUseCase,
            getMessagesUseCase = getMessagesUseCase,
            getLastMessageUseCase = getLastMessageUseCase,
            sendMessageUseCase = sendMessageUseCase,
            createConversationUseCase = createConversationUseCase,
            updateMessageUseCase = updateMessageUseCase,
            getUnreadMessagesUseCase = getUnreadMessagesUseCase
        )
    }

    @Test
    fun default_values_are_correct() = runTest {
        // Then
        assertEquals("", chatViewModel.textToSend)
    }

    @Test
    fun update_text_to_send_should_update_text_to_send() {
        // Given
        val text = "Hello"

        // When
        chatViewModel.updateTextToSend(text)

        // Then
        assertEquals(text, chatViewModel.textToSend)
    }

    @Test
    fun send_message_should_send_message() {
        // Given
        chatViewModel.updateTextToSend("Hello")

        // When
        chatViewModel.sendMessage()

        // Then
        coVerify { sendMessageUseCase(any()) }
    }

    @Test
    fun send_message_should_not_send_message_when_text_is_empty() {
        // When
        chatViewModel.sendMessage()

        // Then
        coVerify(exactly = 0) { sendMessageUseCase(any()) }
    }

    @Test
    fun send_message_should_reset_text_to_send() {
        // Given
        chatViewModel.updateTextToSend("Hello")

        // When
        chatViewModel.sendMessage()

        // Then
        assertEquals("", chatViewModel.textToSend)
    }

    @Test
    fun send_message_should_create_conversation_when_it_is_not_created() {
        // Given
        val conversation = conversationUIFixture.copy(state = ConversationState.NOT_CREATED)
        chatViewModel = ChatViewModel(
            conversation = conversation,
            getCurrentUserUseCase = getCurrentUserUseCase,
            getMessagesUseCase = getMessagesUseCase,
            getLastMessageUseCase = getLastMessageUseCase,
            sendMessageUseCase = sendMessageUseCase,
            createConversationUseCase = createConversationUseCase,
            updateMessageUseCase = updateMessageUseCase,
            getUnreadMessagesUseCase = getUnreadMessagesUseCase
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
        val conversation = conversationUIFixture.copy(state = ConversationState.CREATED)
        chatViewModel = ChatViewModel(
            conversation = conversation,
            getCurrentUserUseCase = getCurrentUserUseCase,
            getMessagesUseCase = getMessagesUseCase,
            getLastMessageUseCase = getLastMessageUseCase,
            sendMessageUseCase = sendMessageUseCase,
            createConversationUseCase = createConversationUseCase,
            updateMessageUseCase = updateMessageUseCase,
            getUnreadMessagesUseCase = getUnreadMessagesUseCase
        )
        chatViewModel.updateTextToSend("Hello")

        // When
        chatViewModel.sendMessage()

        // Then
        coVerify(exactly = 0) { createConversationUseCase(conversation) }
        coVerify { sendMessageUseCase(any()) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun send_message_should_throw_exception_when_current_user_is_null() {
        // Given
        every { getCurrentUserUseCase() } returns MutableStateFlow(null)
        chatViewModel = ChatViewModel(
            conversation = conversationUIFixture,
            getCurrentUserUseCase = getCurrentUserUseCase,
            getMessagesUseCase = getMessagesUseCase,
            getLastMessageUseCase = getLastMessageUseCase,
            sendMessageUseCase = sendMessageUseCase,
            createConversationUseCase = createConversationUseCase,
            updateMessageUseCase = updateMessageUseCase,
            getUnreadMessagesUseCase = getUnreadMessagesUseCase
        )
        chatViewModel.updateTextToSend("Hello")

        // When
        chatViewModel.sendMessage()
    }
}