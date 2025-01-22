package com.upsaclay.message.presentation.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.gson.Gson
import com.upsaclay.message.presentation.components.UserItem
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel

@Composable
fun CreateGroupConversationScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    conversationViewModel: ConversationViewModel
) {
    val users = conversationViewModel.users.collectAsState(emptyList()).value
    val gson = Gson()

    LazyColumn(modifier = modifier) {
        items(users) { user ->
            UserItem(
                user = user,
                onClick = {
                    val userJson = gson.toJson(user)
                    navController.navigate(com.upsaclay.common.domain.model.Screen.CHAT.route + "?user=$userJson")
                }
            )
        }
    }
}