package com.upsaclay.message.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.previewText
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.R
import com.upsaclay.message.domain.conversationUIFixture
import com.upsaclay.message.domain.entity.ChatEvent
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.messageFixture
import com.upsaclay.message.domain.messagesFixture
import com.upsaclay.message.presentation.components.ChatTopBar
import com.upsaclay.message.presentation.components.MessageInput
import com.upsaclay.message.presentation.components.NewMessageIndicator
import com.upsaclay.message.presentation.components.ReceiveMessageItem
import com.upsaclay.message.presentation.components.SentMessageItem
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun ChatScreen(
    conversation: Conversation,
    navController: NavController,
    chatViewModel: ChatViewModel = koinViewModel<ChatViewModel>(
        parameters = { parametersOf(conversation) }
    )
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val messages by chatViewModel.messages.collectAsState(emptyList())
    val newMessage by chatViewModel.event
        .mapNotNull { (it as? ChatEvent.NewMessage)?.message }
        .collectAsState(null)
    val text by chatViewModel.text.collectAsState()

    Scaffold(
        topBar = {
            ChatTopBar(
                navController = navController,
                interlocutor = conversation.interlocutor,
                onClickBack = {
                    keyboardController?.hide()
                }
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
            MessageFeed(
                modifier = Modifier.weight(1f),
                messages = messages,
                interlocutor = conversation.interlocutor,
                newMessage = newMessage
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            MessageInput(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = { chatViewModel.updateTextToSend(it) },
                onSendClick = { chatViewModel.sendMessage() }
            )
        }
    }
}

@Composable
private fun MessageFeed(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    interlocutor: User,
    newMessage: Message?
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showNewMessageIndicator by remember { mutableStateOf(false) }
    val isAtBottom = remember {
        derivedStateOf { scrollState.firstVisibleItemIndex == 0 }
    }

    LaunchedEffect(newMessage) {
        when {
            scrollState.firstVisibleItemIndex <= 1 &&
                    scrollState.layoutInfo.visibleItemsInfo.size < messages.size ->
                scrollState.animateScrollToItem(0)

            scrollState.firstVisibleItemIndex > 1 && newMessage?.senderId == interlocutor.id ->
                showNewMessageIndicator = true
        }
    }

    if (isAtBottom.value) {
        showNewMessageIndicator = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState
        ) {
            itemsIndexed(messages) { index, message ->
                val isSender = message.senderId != interlocutor.id
                val isFirstMessage = index == messages.size - 1
                val isLastMessage = index == 0
                val previousMessage = if (index + 1 < messages.size) messages[index + 1] else null

                val previousSenderId = previousMessage?.senderId ?: ""
                val sameSender = previousSenderId == message.senderId
                val showSeenMessage = isLastMessage && isSender && message.seen?.value == true

                val sameTime = previousMessage?.let {
                    Duration.between(it.date, message.date).toMinutes() < 2L
                } ?: false

                val sameDay = previousMessage?.let {
                    Duration.between(it.date, message.date).toDays() < 2L
                } ?: false

                val displayProfilePicture = !sameTime || isFirstMessage || !sameSender

                if (isSender) {
                    SentMessageItem(
                        modifier = Modifier.testTag(stringResource(R.string.chat_screen_send_message_item_tag)),
                        message = message,
                        showSeen = showSeenMessage
                    )
                } else {
                    ReceiveMessageItem(
                        modifier = Modifier.testTag(stringResource(R.string.chat_screen_receive_message_item_tag)),
                        message = message,
                        displayProfilePicture = displayProfilePicture,
                        profilePictureUrl = interlocutor.profilePictureUrl
                    )
                }

                if (isFirstMessage || !sameDay) {
                    val topPadding = if (isFirstMessage) {
                        MaterialTheme.spacing.default
                    } else {
                        MaterialTheme.spacing.mediumLarge
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = topPadding, bottom = MaterialTheme.spacing.mediumLarge)
                            .fillMaxWidth(),
                        text = FormatLocalDateTimeUseCase.formatDayMonthYear(message.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.previewText,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Spacer(modifier = Modifier.height(messagePadding(sameSender, sameTime)))
                }
            }
        }

        if (showNewMessageIndicator) {
            NewMessageIndicator(modifier = Modifier.align(Alignment.BottomCenter)) {
                scope.launch { scrollState.animateScrollToItem(0) }
            }
        }
    }
}

@Composable
private fun messagePadding(sameSender: Boolean, sameTime: Boolean): Dp =
    if (sameSender && sameTime) 1.dp else MaterialTheme.spacing.small

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ChatScreenPreview() {
    var text by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf(messageFixture)) }
    var id by remember { mutableIntStateOf(10) }
    var newMessage by remember { mutableStateOf<Message?>(null) }

    GedoiseTheme {
        Scaffold(
            topBar = {
                ChatTopBar(
                    navController = rememberNavController(),
                    interlocutor = conversationUIFixture.interlocutor,
                    onClickBack = { }
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
                MessageFeed(
                    modifier = Modifier.weight(1f),
                    messages = messages,
                    interlocutor = conversationUIFixture.interlocutor,
                    newMessage = messageFixture,
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                MessageInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    onSendClick = {
                        id++
                        if (text.isBlank()) return@MessageInput
                        messages = messages.toMutableList().apply {
                            add(
                                Message(
                                    id = id,
                                    conversationId = 1,
                                    senderId = "senderId",
                                    recipientId = userFixture2.id,
                                    content = text,
                                    date = LocalDateTime.now(),
                                    state = MessageState.SENT
                                )
                            )
                            sortedByDescending { it.date }
                        }
                        newMessage = messagesFixture.first()
                        text = ""
                    }
                )
            }
        }
    }
}