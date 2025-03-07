package com.upsaclay.message.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.presentation.components.ClickableItem
import com.upsaclay.common.presentation.components.LoadingDialog
import com.upsaclay.common.presentation.components.SensibleActionDialog
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.R
import com.upsaclay.message.domain.conversationsUIFixture
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.ConvertConversationJsonUseCase
import com.upsaclay.message.presentation.components.ConversationItem
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    conversationViewModel: ConversationViewModel = koinViewModel()
) {
    val conversations by conversationViewModel.conversations.collectAsState(emptyList())
    val screenState by conversationViewModel.screenState.collectAsState()
    val context = LocalContext.current

    var conversationClicked by remember { mutableStateOf<ConversationUI?>(null) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val textColor = if (isSystemInDarkTheme()) GedoiseColor.PreviewTextDark else GedoiseColor.PreviewTextLight

    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConversationDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }

    val hideBottomSheet = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
            }
        }
    }

    val showSnackBar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    LaunchedEffect(screenState) {
        when (screenState) {
            ConversationScreenState.ERROR -> {
                showLoadingDialog = false
                showSnackBar(context.getString(com.upsaclay.common.R.string.occurred_error))
            }

            ConversationScreenState.SUCCESS -> {
                showSnackBar(context.getString(R.string.conversation_deleted))
            }

            else -> {}
        }
    }

    if (showLoadingDialog) {
        LoadingDialog()
    }

    if (showDeleteConversationDialog) {
        SensibleActionDialog(
            title = stringResource(id = R.string.delete_conversation_dialog_title),
            text = stringResource(id = R.string.delete_conversation_dialog_message),
            confirmText = stringResource(id = com.upsaclay.common.R.string.delete),
            onConfirm = {
                showDeleteConversationDialog = false
                conversationClicked?.let { conversationViewModel.deleteConversation(it) }
            },
            onCancel = { showDeleteConversationDialog  = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (conversations.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = MaterialTheme.spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.no_conversation),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                TextButton(
                    contentPadding = PaddingValues(MaterialTheme.spacing.default),
                    modifier = Modifier.height(MaterialTheme.spacing.large),
                    onClick = {
                        navController.navigate(Screen.CREATE_CONVERSATION.route)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.new_conversation),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            LazyColumn {
                items(conversations) { conversation ->
                    ConversationItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(stringResource(id = R.string.conversation_screen_conversation_item_tag)),
                        conversation = conversation,
                        onClick = {
                            navController.navigate(
                                Screen.CHAT.route + "?conversation=${ConvertConversationJsonUseCase(conversation)}"
                            )
                        },
                        onLongClick = {
                            conversationClicked = conversation
                            showBottomSheet = true
                        }
                    )
                }
            }
        }

        CreateConversationFAB(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .testTag(stringResource(id = R.string.conversation_screen_create_conversation_button_tag)),
            onClick = { navController.navigate(Screen.CREATE_CONVERSATION.route) },
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
            ) {
                ClickableItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                        Text(
                            text = stringResource(id = com.upsaclay.common.R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        hideBottomSheet()
                        showDeleteConversationDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            }
        }
    }
}


@Composable
private fun CreateConversationFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier
            .padding(MaterialTheme.spacing.medium)
            .zIndex(2000f),
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_message_add),
            contentDescription = stringResource(id = R.string.ic_fab_button_add_description)
        )
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
private fun ConversationsScreenPreview() {
    val conversations = conversationsUIFixture.sortedByDescending {
        it.lastMessage?.date ?: it.createdAt
    }

    GedoiseTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (conversations.isEmpty()) {
                FlowRow(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_conversation),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))

                    TextButton(
                        contentPadding = PaddingValues(MaterialTheme.spacing.default),
                        modifier = Modifier.height(MaterialTheme.spacing.large),
                        onClick = {}
                    ) {
                        Text(
                            text = stringResource(id = R.string.new_conversation),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(conversations) { conversation ->
                        ConversationItem(
                            modifier = Modifier.fillMaxWidth(),
                            conversation = conversation,
                            onClick = { },
                            onLongClick = {  }
                        )
                    }
                }
            }

            CreateConversationFAB(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = { }
            )
        }
    }
}