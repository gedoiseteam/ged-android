package com.upsaclay.message

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.message.domain.conversationFixture
import com.upsaclay.message.domain.messageFixture
import com.upsaclay.message.domain.messageFixture2
import com.upsaclay.message.domain.messagesFixture
import com.upsaclay.message.presentation.chat.ChatDestination
import com.upsaclay.message.presentation.chat.ChatViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val chatViewModel: ChatViewModel = mockk()
    private val uiState = ChatViewModel.ChatUiState(
        messages = emptyList(),
        conversation = conversationFixture,
        text = "",
    )

    @Before
    fun setUp() {
        every { chatViewModel.uiState } returns MutableStateFlow(uiState)
        every { chatViewModel.event } returns MutableSharedFlow()
        every { chatViewModel.sendMessage() } returns Unit
    }

    @Test
    fun sentMessageItem_should_be_displayed_user_is_sender() {
        // Given
        every { chatViewModel.uiState } returns MutableStateFlow(
            uiState.copy(messages = listOf(messageFixture))
        )

        // When
        rule.setContent {
            ChatDestination(
                conversation = conversationFixture,
                onBackClick = {},
                viewModel = chatViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.chat_screen_send_message_item_tag) + 0)
            .assertExists()

    }

    @Test
    fun receiveMessageItem_should_be_displayed_user_is_sender() {
        // Given
        every { chatViewModel.uiState } returns MutableStateFlow(
            uiState.copy(messages = listOf(messageFixture2))
        )

        // When
        rule.setContent {
            ChatDestination(
                conversation = conversationFixture,
                onBackClick = {},
                viewModel = chatViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.chat_screen_receive_message_item_tag) + 0)
            .assertExists()

    }
}