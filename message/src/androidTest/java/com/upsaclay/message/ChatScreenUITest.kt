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
        every { chatViewModel.textToSend } returns ""
        every { chatViewModel.sendMessage() } returns Unit
    }
}