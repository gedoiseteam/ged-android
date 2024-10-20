package com.upsaclay.message.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.presentation.components.OverlayCircularLoadingScreen
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.R
import com.upsaclay.message.domain.model.Message
import com.upsaclay.message.presentation.components.ChatTopBar
import com.upsaclay.message.presentation.components.MessageInput
import com.upsaclay.message.presentation.components.ReceiveMessageItem
import com.upsaclay.message.presentation.components.SentMessageItem
import com.upsaclay.message.presentation.viewmodel.ChatViewModel
import com.upsaclay.message.utils.conversationFixture
import com.upsaclay.message.utils.messagesFixture
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(
    interlocutorId: Int,
    navController: NavController,
    chatViewModel: ChatViewModel = koinViewModel()
) {
    val conversation = chatViewModel.conversation.collectAsState(null).value

    LaunchedEffect(Unit) {
        chatViewModel.getConversation(interlocutorId)
    }

    if (conversation != null) {
        Scaffold(
            topBar = {
                ChatTopBar(
                    navController = navController,
                    interlocutor = conversation.interlocutor
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(
                    top = innerPadding.calculateTopPadding(),
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.small
                )
            ) {
                DisplayMessageSection(
                    modifier = Modifier.weight(1f),
                    messages = conversation.messages,
                    interlocutor = conversation.interlocutor
                )

                MessageInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = chatViewModel.messageToSend,
                    onValueChange = { chatViewModel.updateMessageToSend(it) },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.message_placeholder),
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    },
                    onSendClick = {
                        chatViewModel.sendMessage()
                        chatViewModel.resetMessageToSend()
                    },
                    showSendButton = chatViewModel.messageToSend.isNotBlank()
                )
            }
        }
    } else {
        OverlayCircularLoadingScreen(scale = 1f)
    }
}

@Composable
private fun DisplayMessageSection(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    interlocutor: User
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        reverseLayout = true,
    ) {
        if(messages.isNotEmpty()) {
            itemsIndexed(messages) { index, message ->
                val sameSender = index > 0 &&
                        ((message.sentByUser && messages[index - 1].sentByUser) ||
                                (!message.sentByUser && !messages[index - 1].sentByUser))

                val spacerHeight: Dp = if (sameSender) {
                    MaterialTheme.spacing.extraSmall
                } else {
                    MaterialTheme.spacing.smallMedium
                }

                Spacer(modifier = Modifier.height(spacerHeight))

                if (message.sentByUser) {
                    SentMessageItem(text = message.content)
                } else {
                    val displayProfilePicture: Boolean = index == 0 || !sameSender

                    val receiveMessageItemModifier = if (displayProfilePicture) {
                        Modifier
                    } else {
                        Modifier.padding(start = MaterialTheme.spacing.extraLarge)
                    }

                    ReceiveMessageItem(
                        modifier = receiveMessageItemModifier,
                        message = message,
                        displayProfilePicture = displayProfilePicture,
                        profilePictureUrl = interlocutor.profilePictureUrl
                    )
                }
            }
        }
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview
@Composable
private fun ChatScreenPreview() {
    var text by remember { mutableStateOf("") }

    GedoiseTheme {
        Scaffold(
            topBar = {
                ChatTopBar(
                    navController = rememberNavController(),
                    interlocutor = conversationFixture.interlocutor
                )
            }
        ) { innerPadding ->
            Column {
                Column(
                    modifier = Modifier.padding(
                        top = innerPadding.calculateTopPadding(),
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        bottom = MaterialTheme.spacing.small
                    )
                ) {
                    DisplayMessageSection(
                        modifier = Modifier.weight(1f),
                        messages = messagesFixture,
                        interlocutor = conversationFixture.interlocutor
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    MessageInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = text,
                        onValueChange = { text = it },
                        placeholder = { Text(stringResource(id = R.string.message_placeholder)) },
                        onSendClick = {},
                        showSendButton = text.isNotBlank()
                    )
                }
            }
        }
    }
}