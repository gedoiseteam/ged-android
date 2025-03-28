package com.upsaclay.message.presentation.screens

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usersFixture
import com.upsaclay.common.presentation.components.LinearProgressBar
import com.upsaclay.common.presentation.components.SmallTopBarBack
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.previewText
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.R
import com.upsaclay.message.domain.entity.ConversationEvent
import com.upsaclay.message.domain.entity.MessageScreenRoute
import com.upsaclay.message.presentation.components.UserItem
import com.upsaclay.message.presentation.viewmodels.CreateConversationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateConversationScreen(
    navController: NavController,
    createConversationViewModel: CreateConversationViewModel = koinViewModel()
) {
    val users by createConversationViewModel.users.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        createConversationViewModel.event.collectLatest { event ->
            loading = event == ConversationEvent.Loading
        }
    }

    Scaffold(
        topBar = {
            SmallTopBarBack(
                onBackClick = { navController.popBackStack() },
                title = stringResource(id = com.upsaclay.message.R.string.new_conversation)
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(top = contentPadding.calculateTopPadding())
        ) {
            if (loading) {
                LinearProgressBar(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn {
                if (users.isNotEmpty()) {
                    items(users) { user ->
                        UserItem(
                            user = user,
                            onClick = {
                                scope.launch {
                                    val conversation = createConversationViewModel.getConversation(user.id)
                                        ?: createConversationViewModel.generateConversation(user)

                                    navController.navigate(MessageScreenRoute.Chat(conversation).route) {
                                        popUpTo(MessageScreenRoute.CreateConversation.route) {
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
                            color = MaterialTheme.colorScheme.previewText
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
    val users: List<User> = usersFixture + usersFixture
    var loading by remember { mutableStateOf(true) }

    GedoiseTheme {
        Scaffold(
            topBar = {
                SmallTopBarBack(
                    onBackClick = { },
                    title = stringResource(id = R.string.new_conversation)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            ) {
                if (loading) {
                    LinearProgressBar(modifier = Modifier.fillMaxWidth())
                }

                LazyColumn {
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
                                color = MaterialTheme.colorScheme.previewText
                            )
                        }
                    }
                }
            }
        }
    }
}