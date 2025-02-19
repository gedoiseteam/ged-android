package com.upsaclay.message.presentation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usersFixture
import com.upsaclay.common.presentation.components.CircularProgressBar
import com.upsaclay.common.presentation.components.SmallTopBarBack
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.R
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.usecase.ConvertConversationJsonUseCase
import com.upsaclay.message.presentation.components.UserItem
import com.upsaclay.message.presentation.viewmodels.CreateConversationViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateConversationScreen(
    navController: NavController,
    createConversationViewModel: CreateConversationViewModel = koinViewModel()
) {
    val users by createConversationViewModel.users.collectAsState(emptyList())
    val screenState by createConversationViewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            SmallTopBarBack(
                onBackClick = { navController.popBackStack() },
                title = stringResource(id = com.upsaclay.message.R.string.new_conversation)
            )
        }
    ) { contentPadding ->
        if (screenState == ConversationScreenState.LOADING) {
            CircularProgressBar()
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(top = contentPadding.calculateTopPadding())
            ) {
                if (users.isNotEmpty()) {
                    items(users) { user ->
                        UserItem(
                            user = user,
                            onClick = {
                                createConversationViewModel.getConversationUser(user.id)?.let {
                                    navController.navigate(
                                        Screen.CHAT.route + "?conversation=${ConvertConversationJsonUseCase(it)}"
                                    ) {
                                        popUpTo(Screen.CREATE_CONVERSATION.route) {
                                            inclusive = true
                                        }
                                    }
                                } ?: run {
                                    val conversationJson = createConversationViewModel.generateConversationJson(user)
                                    navController.navigate(Screen.CHAT.route + "?conversation=$conversationJson") {
                                        popUpTo(Screen.CREATE_CONVERSATION.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        )
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = com.upsaclay.common.R.string.no_user_found),
                            textAlign = TextAlign.Center,
                            color = GedoiseColor.PreviewText
                        )
                    }
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

@Preview(showBackground = true)
@Composable
private fun CreateConversationScreenPreview() {
    val users: List<User> = usersFixture

    GedoiseTheme {
        Scaffold(
            topBar = {
                SmallTopBarBack(
                    onBackClick = { },
                    title = stringResource(id = R.string.new_conversation)
                )
            }
        ) { innerPadding ->
            LazyColumn(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                if (users.isNotEmpty()) {
                    items(users) { user ->
                        UserItem(
                            user = user,
                            onClick = { }
                        )
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = com.upsaclay.common.R.string.no_user_found),
                            textAlign = TextAlign.Center,
                            color = GedoiseColor.PreviewText
                        )
                    }
                }
            }
        }
    }
}