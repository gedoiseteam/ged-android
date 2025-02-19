package com.upsaclay.message

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.message.domain.conversationUIFixture
import com.upsaclay.message.domain.conversationUserFixture
import com.upsaclay.message.domain.conversationsUIFixture
import com.upsaclay.message.domain.messageFixture
import com.upsaclay.message.presentation.screens.ChatScreen
import com.upsaclay.message.presentation.screens.ConversationScreen
import com.upsaclay.message.presentation.screens.CreateConversationScreen
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConversationScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val conversationViewModel: ConversationViewModel = mockk()

    @Before
    fun setUp() {
        every { conversationViewModel.conversations } returns flowOf(conversationsUIFixture)
    }

    @Test
    fun conversations_should_be_displayed_when_no_empty() {
        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel
            )
        }

        // Then
        rule.onAllNodesWithTag(rule.activity.getString(R.string.conversation_screen_conversation_item_tag))
            .apply {
                fetchSemanticsNodes().forEachIndexed { i, _ ->
                    get(i).assert(hasText(conversationsUIFixture[i].interlocutor.fullName))
                }
            }
    }

    @Test
    fun text_should_be_displayed_when_conversations_are_empty() {
        // Given
        every { conversationViewModel.conversations } returns flowOf(emptyList())

        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.no_conversation)).assertExists()
        rule.onNodeWithText(rule.activity.getString(R.string.new_conversation)).assertExists()
    }

    @Test
    fun create_conversations_button_should_be_displayed() {
        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.conversation_screen_create_conversation_button_tag)).assertExists()
    }

    @Test
    fun empty_conversations_item_should_be_displayed_when_no_last_message() {
        // Given
        every { conversationViewModel.conversations } returns flowOf(listOf(conversationUIFixture.copy(lastMessage = null)))

        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel
            )
        }

        // Then
        rule.onNodeWithTag(
            rule.activity.getString(R.string.conversation_screen_empty_conversation_item_tag),
            useUnmergedTree = true
        ).assertExists()
    }

    @Test
    fun read_conversations_item_should_be_displayed_when_last_message_is_read() {
        // Given
        every { conversationViewModel.conversations } returns flowOf(listOf(conversationUIFixture))

        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel
            )
        }

        // Then
        rule.onNodeWithTag(
            rule.activity.getString(R.string.conversation_screen_read_conversation_item_tag),
            useUnmergedTree = true
        ).assertExists()
    }

    @Test
    fun unread_conversations_item_should_be_displayed_when_last_message_is_not_read_and_not_sent_by_interlocutor() {
        // Given
        val lastMessage = messageFixture.copy(isRead = false, senderId = userFixture2.id)
        every { conversationViewModel.conversations } returns flowOf(listOf(conversationUIFixture.copy(lastMessage = lastMessage)))

        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel
            )
        }

        // Then
        rule.onNodeWithTag(
            rule.activity.getString(R.string.conversation_screen_unread_conversation_item_tag),
            useUnmergedTree = true
        ).assertExists()
    }

    @Test
    fun navigate_to_create_conversation_screen_when_create_conversation_button_is_clicked() {
        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Screen.CONVERSATION.route) {
                composable(Screen.CONVERSATION.route) {
                    ConversationScreen(
                        navController = navController,
                        conversationViewModel = conversationViewModel
                    )
                }

                composable(Screen.CREATE_CONVERSATION.route) {
                    CreateConversationScreen(
                        navController = navController,
                        createConversationViewModel = mockk()
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.conversation_screen_create_conversation_button_tag)).performClick()

        // Then
        Assert.assertEquals(Screen.CREATE_CONVERSATION.route, navController.currentDestination?.route)
    }

    @Test
    fun navigate_to_chat_screen_when_conversation_item_is_clicked() {
        // Given
        every { conversationViewModel.conversations } returns flowOf(listOf(conversationUIFixture))

        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Screen.CONVERSATION.route) {
                composable(Screen.CONVERSATION.route) {
                    ConversationScreen(
                        navController = navController,
                        conversationViewModel = conversationViewModel
                    )
                }

                composable(Screen.CHAT.route) {
                    ChatScreen(
                        conversation = conversationUIFixture,
                        navController = navController,
                        chatViewModel = mockk()
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.conversation_screen_conversation_item_tag)).performClick()

        // Then
        Assert.assertEquals(Screen.CHAT.route, navController.currentDestination?.route)
    }
}