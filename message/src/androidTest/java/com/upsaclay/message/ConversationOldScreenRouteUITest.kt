package com.upsaclay.message

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
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
import androidx.paging.PagingData
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.message.domain.conversationFixture
import com.upsaclay.message.domain.conversationUIFixture
import com.upsaclay.message.domain.conversationsUIFixture
import com.upsaclay.message.domain.entity.MessageScreenRoute
import com.upsaclay.message.domain.messageFixture
import com.upsaclay.message.presentation.screens.ChatScreen
import com.upsaclay.message.presentation.screens.ConversationScreen
import com.upsaclay.message.presentation.screens.CreateConversationScreen
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConversationOldScreenRouteUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val conversationViewModel: ConversationViewModel = mockk()

    @Before
    fun setUp() {
        every { conversationViewModel.conversations } returns flowOf(PagingData.from(conversationsUIFixture))
        every { conversationViewModel.event } returns MutableSharedFlow()
    }

    @Test
    fun conversations_should_be_displayed_when_no_empty() {
        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel,
                snackbarHostState = SnackbarHostState()
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
        every { conversationViewModel.conversations } returns flowOf(PagingData.empty())

        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel,
                snackbarHostState = SnackbarHostState(),
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
                conversationViewModel = conversationViewModel,
                snackbarHostState = SnackbarHostState(),
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.conversation_screen_create_conversation_button_tag)).assertExists()
    }

    @Test
    fun empty_conversations_item_should_be_displayed_when_no_last_message() {
        // Given
        every { conversationViewModel.conversations } returns
                flowOf(PagingData.from(listOf(conversationUIFixture.copy(lastMessage = null))))

        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel,
                snackbarHostState = SnackbarHostState(),
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
        every { conversationViewModel.conversations } returns flowOf(PagingData.from(listOf(conversationUIFixture)))

        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel,
                snackbarHostState = SnackbarHostState()
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
        val lastMessage = messageFixture.copy(seen = null, senderId = userFixture2.id)
        every { conversationViewModel.conversations } returns
                flowOf(PagingData.from(listOf(conversationUIFixture.copy(lastMessage = lastMessage))))

        // When
        rule.setContent {
            ConversationScreen(
                navController = navController,
                conversationViewModel = conversationViewModel,
                snackbarHostState = SnackbarHostState()
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
            NavHost(navController = navController, startDestination = MessageScreenRoute.Conversation.route) {
                composable(MessageScreenRoute.Conversation.route) {
                    ConversationScreen(
                        navController = navController,
                        conversationViewModel = conversationViewModel,
                        snackbarHostState = SnackbarHostState()
                    )
                }

                composable(MessageScreenRoute.CreateConversation.route) {
                    CreateConversationScreen(
                        navController = navController,
                        createConversationViewModel = mockk()
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.conversation_screen_create_conversation_button_tag)).performClick()

        // Then
        Assert.assertEquals(MessageScreenRoute.CreateConversation.route, navController.currentDestination?.route)
    }

    @Test
    fun navigate_to_chat_screen_when_conversation_item_is_clicked() {
        // Given
        every { conversationViewModel.conversations } returns flowOf(PagingData.from(listOf(conversationUIFixture)))

        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = MessageScreenRoute.Conversation.route) {
                composable(MessageScreenRoute.Conversation.route) {
                    ConversationScreen(
                        navController = navController,
                        conversationViewModel = conversationViewModel,
                        snackbarHostState = SnackbarHostState()
                    )
                }

                composable(MessageScreenRoute.Chat.HARD_ROUTE) {
                    ChatScreen(
                        conversation = conversationFixture,
                        navController = navController,
                        chatViewModel = mockk()
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.conversation_screen_conversation_item_tag)).performClick()

        // Then
        Assert.assertEquals(MessageScreenRoute.Chat.HARD_ROUTE, navController.currentDestination?.route)
    }
}