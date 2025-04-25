package com.upsaclay.message

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule

class ChatScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val chatViewModel: ChatViewModel = mockk()

    @Before
    fun setUp() {
        every { chatViewModel.text } returns MutableStateFlow("")
        every { chatViewModel.sendMessage() } returns Unit
    }
}