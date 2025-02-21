package com.upsaclay.message.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.FormatLocalDateTimeUseCase
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.R
import com.upsaclay.message.domain.conversationUIFixture
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.messagesFixture
import com.upsaclay.message.presentation.components.ChatTopBar
import com.upsaclay.message.presentation.components.MessageInput
import com.upsaclay.message.presentation.components.ReceiveMessageItem
import com.upsaclay.message.presentation.components.SentMessageItem
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChatScreen(
    conversation: ConversationUI,
    navController: NavController,
    chatViewModel: ChatViewModel = koinViewModel<ChatViewModel>(
        parameters = { parametersOf(conversation) }
    )
) {
    val messages by chatViewModel.messages.collectAsState(emptyList())

    Scaffold(
        topBar = {
            ChatTopBar(
                navController = navController,
                interlocutor = chatViewModel.conversation.interlocutor
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                start = MaterialTheme.spacing.medium,
                end = MaterialTheme.spacing.medium,
                bottom = MaterialTheme.spacing.medium
            )
        ) {
            MessageSection(
                modifier = Modifier.weight(1f),
                messages = messages,
                interlocutor = chatViewModel.conversation.interlocutor
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            MessageInput(
                modifier = Modifier.fillMaxWidth(),
                value = chatViewModel.textToSend,
                onValueChange = { chatViewModel.updateTextToSend(it) },
                onSendClick = { chatViewModel.sendMessage() }
            )
        }
    }
}

@Composable
private fun MessageSection(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    interlocutor: User
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        reverseLayout = true
    ) {
        if (messages.isNotEmpty()) {
            itemsIndexed(messages) { index, message ->
                val currentUserSender = message.senderId != interlocutor.id
                val firstMessage = index == messages.size - 1
                val lastMessage = index == 0
                val previousSenderId = if (!firstMessage) messages[index + 1].senderId else ""
                val sameSender = previousSenderId == message.senderId
                val nextSenderId = if (!lastMessage) messages[index - 1].senderId else ""
                val showSeenMessage = lastMessage && currentUserSender && message.seen

                val sameTime = if (!firstMessage) {
                    message.date
                        .withSecond(0)
                        .withNano(0)
                        .isEqual(
                            messages[index + 1].date
                                .withSecond(0)
                                .withNano(0)
                        )
                } else false

                val sameDay = if (!firstMessage) {
                    message.date
                        .toLocalDate()
                        .isEqual(messages[index + 1].date.toLocalDate())
                }
                else false

                val displayProfilePicture =
                    !sameTime || (!currentUserSender && message.senderId != nextSenderId )

                if (message.senderId != interlocutor.id) {
                    SentMessageItem(
                        modifier = Modifier.testTag(stringResource(R.string.chat_screen_send_message_item_tag)),
                        message = message,
                        seen = showSeenMessage
                    )
                } else {
                    ReceiveMessageItem(
                        modifier = Modifier.testTag(stringResource(R.string.chat_screen_receive_message_item_tag)),
                        message = message,
                        displayProfilePicture = displayProfilePicture,
                        profilePictureUrl = interlocutor.profilePictureUrl
                    )
                }

                if (firstMessage || !sameDay) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = MaterialTheme.spacing.mediumLarge)
                            .fillMaxWidth(),
                        text = FormatLocalDateTimeUseCase.formatDayMonthYear(message.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = GedoiseColor.PreviewTextLight,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Spacer(modifier = Modifier.height(messagePadding(sameSender, sameTime)))
                }
            }
        }
    }
}

@Composable
private fun messagePadding(
    sameSender: Boolean,
    sameTime: Boolean
): Dp {
    return if (sameSender && sameTime) {
        2.dp
    } else {
        MaterialTheme.spacing.smallMedium
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatScreenPreview() {
    var text by remember { mutableStateOf("") }

    GedoiseTheme {
        Scaffold(
            topBar = {
                ChatTopBar(
                    navController = rememberNavController(),
                    interlocutor = conversationUIFixture.interlocutor
                )
            }
        ) { innerPadding ->
            Column {
                Column(
                    modifier = Modifier.padding(
                        top = innerPadding.calculateTopPadding(),
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        bottom = MaterialTheme.spacing.medium
                    )
                ) {
                    MessageSection(
                        modifier = Modifier.weight(1f),
                        messages = messagesFixture,
                        interlocutor = conversationUIFixture.interlocutor
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    MessageInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = text,
                        onValueChange = { text = it },
                        onSendClick = { }
                    )
                }
            }
        }
    }
}