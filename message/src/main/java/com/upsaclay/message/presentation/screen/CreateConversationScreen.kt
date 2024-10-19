package com.upsaclay.message.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.gson.Gson
import com.upsaclay.common.domain.model.Screen
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.presentation.components.SmallTopBarBack
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.utils.usersFixture
import com.upsaclay.message.R
import com.upsaclay.message.domain.model.ConversationState
import com.upsaclay.message.presentation.components.UserItem
import com.upsaclay.message.presentation.viewmodel.ConversationViewModel

@Composable
fun CreateConversationScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    conversationViewModel: ConversationViewModel
) {
    val users = conversationViewModel.users.collectAsState(emptyList()).value
    val conversationState = conversationViewModel.conversationState.collectAsState(ConversationState.DEFAULT).value
    var userClicked: User? by remember { mutableStateOf(null) }

    if(conversationState == ConversationState.CREATED) {
        navController.navigate(Screen.CHAT.route + "?interlocutorId=${userClicked!!.id}") {
            popUpTo(Screen.CREATE_CONVERSATION.route) { inclusive = true }
        }
    }

    LazyColumn(modifier = modifier) {
        items(users) { user ->
            UserItem(
                user = user,
                onClick = {
                    userClicked = user
                    conversationViewModel.createNewConversation(user)
                }
            )
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
                items(usersFixture) { user ->
                    UserItem(user = user, onClick = { })
                }
            }
        }
    }
}