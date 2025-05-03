package com.upsaclay.message.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.Phones
import com.upsaclay.common.utils.mediumPadding
import com.upsaclay.message.domain.conversationFixture
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.messagesFixture
import com.upsaclay.message.presentation.chat.ChatViewModel.MessageEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChatDestination(
    conversation: Conversation,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = koinViewModel {
        parametersOf(conversation)
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    val newMessageEvent by viewModel.event
        .mapNotNull { it as? MessageEvent.NewMessage }
        .collectAsState(null)

    ChatScreen(
        conversation = conversation,
        messages = uiState.messages,
        text = uiState.text,
        newMessageEvent = newMessageEvent,
        onTextChange = viewModel::onTextChanged,
        onSendMessage = viewModel::sendMessage,
        onBackClick = onBackClick
    )
}

@Composable
private fun ChatScreen(
    conversation: Conversation,
    messages: List<Message>,
    text: String,
    newMessageEvent: MessageEvent.NewMessage?,
    onTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onBackClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            ChatTopBar(
                navController = rememberNavController(),
                interlocutor = conversation.interlocutor,
                onClickBack = {
                    keyboardController?.hide()
                    onBackClick()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .mediumPadding(paddingValues)
                .fillMaxSize()
        ) {
            MessageFeed(
                modifier = Modifier.weight(1f),
                messages = messages,
                interlocutor = conversation.interlocutor,
                newMessageEvent = newMessageEvent
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            MessageInput(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = onTextChange,
                onSendClick = onSendMessage
            )
        }
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Phones
@Composable
private fun ChatScreenPreview() {
    var text by remember { mutableStateOf("") }

    GedoiseTheme {
        ChatScreen(
            conversation = conversationFixture,
            messages = messagesFixture,
            text = text,
            newMessageEvent = null,
            onTextChange = { text = it },
            onSendMessage = {},
            onBackClick = {}
        )
    }
}