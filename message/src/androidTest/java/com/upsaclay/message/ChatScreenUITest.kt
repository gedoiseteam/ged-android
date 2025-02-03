package com.upsaclay.message

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.message.domain.conversationUIFixture
import com.upsaclay.message.domain.messageFixture
import com.upsaclay.message.domain.messagesFixture
import com.upsaclay.message.presentation.screens.ChatScreen
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val chatViewModel: ChatViewModel = mockk()

    @Before
    fun setUp() {
        every { chatViewModel.messages } returns flowOf(messagesFixture)
        every { chatViewModel.textToSend } returns ""
        every { chatViewModel.conversation } returns conversationUIFixture
        every { chatViewModel.sendMessage() } returns Unit
    }

    @Test
    fun send_message_item_should_be_displayed_when_user_is_sender() {
        // Given
        every { chatViewModel.messages } returns flowOf(listOf(messageFixture))

        // When
        rule.setContent {
            ChatScreen(
                conversation = conversationUIFixture,
                navController = navController,
                chatViewModel = chatViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.chat_screen_send_message_item_tag)).assertExists()
        rule.onNodeWithTag(rule.activity.getString(R.string.chat_screen_message_text_tag)).assert(
            hasText(messageFixture.content)
        )
    }

    @Test
    fun receive_message_item_should_be_displayed_when_user_is_not_sender() {
        // Given
        val message = messageFixture.copy(senderId = conversationUIFixture.interlocutor.id)
        every { chatViewModel.messages } returns flowOf(listOf(message))

        // When
        rule.setContent {
            ChatScreen(
                conversation = conversationUIFixture,
                navController = navController,
                chatViewModel = chatViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.chat_screen_receive_message_item_tag)).assertExists()
        rule.onNodeWithTag(rule.activity.getString(R.string.chat_screen_message_text_tag)).assert(
            hasText(messageFixture.content)
        )
    }
}