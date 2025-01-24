package com.upsaclay.message.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.LocalDateTimeFormatterUseCase
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.presentation.components.ChatTopBar
import com.upsaclay.message.presentation.components.MessageInput
import com.upsaclay.message.presentation.components.ReceiveMessageItem
import com.upsaclay.message.presentation.components.SentMessageItem
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import com.upsaclay.message.conversationFixture
import com.upsaclay.message.messagesFixture
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = koinViewModel()
) {
    val messages by chatViewModel.messages.collectAsState(emptyMap())

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
                bottom = MaterialTheme.spacing.small
            )
        ) {
            MessageSection(
                modifier = Modifier.weight(1f),
                messages = messages.values.toList(),
                interlocutor = chatViewModel.conversation.interlocutor
            )

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
        reverseLayout = true,
    ) {
        if(messages.isNotEmpty()) {
            itemsIndexed(messages) { index, message ->
                val firstMessage = index == 0
                val lastMessage = index == messages.size - 1
                val previousSenderId = if(!firstMessage) messages[index - 1].senderId else ""
                val sameSender = previousSenderId == message.senderId
                val nextSenderId = if(!lastMessage) messages[index + 1].senderId else ""

                val sameTime = if(!firstMessage) {
                    message.date.withSecond(0).withNano(0)
                        .isEqual(
                            messages[index - 1].date
                                .withSecond(0)
                                .withNano(0)
                        )
                } else false

                val sameDay = if(!firstMessage) {
                    message.date.toLocalDate().isEqual(messages[index - 1].date.toLocalDate())
                } else false

                val displayProfilePicture =
                    !sameTime || (message.senderId != nextSenderId && message.senderId == interlocutor.id)

                val spacerHeight: Dp = if (sameSender) {
                    MaterialTheme.spacing.extraSmall
                } else {
                    MaterialTheme.spacing.smallMedium
                }

                if(firstMessage || !sameDay) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        Text(LocalDateTimeFormatterUseCase.formatDayMonthYear(message.date))

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    }
                }

                Spacer(modifier = Modifier.height(spacerHeight))

                if (message.senderId != interlocutor.id) {
                    SentMessageItem(text = message.content)
                } else {
                    val receiveMessageItemModifier =
                        if (displayProfilePicture)
                            Modifier
                        else
                            Modifier.padding(start = MaterialTheme.spacing.extraLarge)

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
                    MessageSection(
                        modifier = Modifier.weight(1f),
                        messages = messagesFixture,
                        interlocutor = conversationFixture.interlocutor
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