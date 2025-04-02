package com.upsaclay.message.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.FormatLocalDateTimeUseCase
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.common.presentation.components.CircularProgressBar
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
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
    val messageItems = chatViewModel.messages.collectAsLazyPagingItems()
    val keyboardController = LocalSoftwareKeyboardController.current
    var newMessage by remember { mutableStateOf<Message?>(null) }

    LaunchedEffect(Unit) {
        chatViewModel.event.collectLatest { event ->
            when (event) {
                is ChatEvent.NewMessage -> newMessage = event.message
            }
        }
    }

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
                messageItems = messageItems,
                interlocutor = conversation.interlocutor,
                newMessage = newMessage ?: messageItems.itemSnapshotList.lastOrNull()
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
private fun MessageFeed(
    modifier: Modifier = Modifier,
    messageItems: LazyPagingItems<Message>,
    interlocutor: User,
    newMessage: Message?
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showNewMessageIndicator by remember { mutableStateOf(false) }

    LaunchedEffect(newMessage) {
        delay(50)
        when {
            listState.firstVisibleItemIndex == 1 &&
                    listState.layoutInfo.visibleItemsInfo.size < messageItems.itemCount -> listState.animateScrollToItem(0)

            listState.firstVisibleItemIndex > 1 && newMessage?.senderId == interlocutor.id -> showNewMessageIndicator = true
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collectLatest { index ->
            if (index == 0) {
                showNewMessageIndicator = false
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            reverseLayout = true,
            state = listState
        ) {
            items(
                count = messageItems.itemCount,
                key = messageItems.itemKey { it.id },
                contentType = messageItems.itemContentType { "Message feed" }
            ) { index ->
                if (messageItems.itemCount > 0) {
                    val message = messageItems[index] ?: return@items
                    val currentUserSender = message.senderId != interlocutor.id
                    val firstMessage = index == messageItems.itemCount - 1
                    val lastMessage = index == 0
                    val previousMessage = if (index + 1 < messageItems.itemCount) messageItems[index + 1] else null

                    val previousSenderId = previousMessage?.senderId ?: ""
                    val sameSender = previousSenderId == message.senderId
                    val showSeenMessage = lastMessage && currentUserSender && message.seen?.value == true

                    val sameTime = previousMessage?.let {
                        Duration.between(it.date, message.date).toMinutes() < 2L
                    } ?: false

                    val sameDay = previousMessage?.let {
                        Duration.between(it.date, message.date).toDays() < 2L
                    } ?: false

                    val displayProfilePicture = !sameTime || firstMessage || !sameSender

                    if (message.senderId != interlocutor.id) {
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

                    if (firstMessage || !sameDay) {
                        val topPadding = if (firstMessage)
                            MaterialTheme.spacing.default
                        else
                            MaterialTheme.spacing.mediumLarge

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

            item {
                when (messageItems.loadState.append) {
                    is LoadState.Loading -> {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressBar()
                        }
                    }

                    else -> {}
                }
            }
        }

        if (showNewMessageIndicator) {
            NewMessageIndicator(modifier = Modifier.align(Alignment.BottomCenter)) {
                scope.launch { listState.animateScrollToItem(0) }
            }
        }
    }
}

@Composable
private fun messagePadding(sameSender: Boolean, sameTime: Boolean): Dp =
    if (sameSender && sameTime) 2.dp else MaterialTheme.spacing.smallMedium

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
                    messageItems = flowOf(PagingData.from(messages.sortedByDescending { it.date })).collectAsLazyPagingItems(),
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