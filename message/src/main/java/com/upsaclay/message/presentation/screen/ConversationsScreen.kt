package com.upsaclay.message.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.upsaclay.common.domain.model.Screen
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.R
import com.upsaclay.message.presentation.components.ConversationItem
import com.upsaclay.message.presentation.viewmodel.ConversationViewModel
import com.upsaclay.message.utils.conversationsFixture
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConversationScreen(navController: NavController, conversationViewModel: ConversationViewModel = koinViewModel()) {
    val conversations = conversationViewModel.conversations.collectAsState(emptyList()).value
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (expanded) {
            val Color = if (isSystemInDarkTheme()) GedoiseColor.tertiaryDark else GedoiseColor.tertiaryLight
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color)
                    .zIndex(1000f)
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = { expanded = !expanded })
                    }
            )
        }

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
                    onClick = { navController.navigate(com.upsaclay.common.domain.model.Screen.CREATE_CONVERSATION.route) }
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
                        modifier = Modifier.fillMaxWidth(),
                        conversation = conversation,
                        onClick = {
//                            navController.navigate(Screen.CHAT.route + "?conversationId=${conversation.id}")
                        },
                        onLongClick = { }
                    )
                }
            }
        }

        FloatingActionButtonSection(
            modifier = Modifier.align(Alignment.BottomEnd),
            expanded = expanded,
            onToggleClick = { expanded = !expanded },
            onNewConversationClick = { navController.navigate(com.upsaclay.common.domain.model.Screen.CREATE_CONVERSATION.route) },
            onNewGroupClick = { navController.navigate(com.upsaclay.common.domain.model.Screen.CREATE_GROUP_CONVERSATION.route) }
        )
    }
}

@Composable
private fun FloatingActionButtonSection(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onToggleClick: () -> Unit,
    onNewConversationClick: () -> Unit,
    onNewGroupClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        label = "rotateFab"
    )

    Column(
        modifier = modifier
            .padding(MaterialTheme.spacing.medium)
            .zIndex(2000f),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = scaleIn(initialScale = 0.5f) + slideIn { fullSize -> IntOffset(0, fullSize.height) },
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallMedium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallMedium)
            ) {
                Text(
                    text = stringResource(id = R.string.group),
                    style = MaterialTheme.typography.labelMedium
                )

                SmallFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.surface,
                    onClick = onNewGroupClick
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_group_add),
                        contentDescription = stringResource(id = R.string.ic_group_add_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (expanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallMedium)
            ) {
                Text(
                    text = stringResource(id = R.string.conversation),
                    style = MaterialTheme.typography.labelMedium
                )

                FloatingActionButton(
                    onClick = onNewConversationClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        modifier = Modifier.rotate(rotation),
                        painter = painterResource(id = R.drawable.ic_message_add),
                        contentDescription = ""
                    )
                }
            }
        } else {
            FloatingActionButton(
                onClick = onToggleClick,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    modifier = Modifier.rotate(rotation),
                    painter = painterResource(id = com.upsaclay.common.R.drawable.ic_add),
                    contentDescription = ""
                )
            }
        }
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
    val conversations = conversationsFixture.sortedByDescending { it.messages.last().date }
//    val conversations = emptyList<ConversationPreview>()
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        label = "rotateFab",
        animationSpec = tween(durationMillis = 200, easing = Ease)
    )

    GedoiseTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (expanded) {
                val Color = if (isSystemInDarkTheme()) GedoiseColor.tertiaryDark else GedoiseColor.tertiaryLight
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color)
                        .zIndex(1000f)
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = { expanded = !expanded })
                        }
                )
            }
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
                            onLongClick = { }
                        )
                    }
                }
            }

            FloatingActionButtonSection(
                modifier = Modifier.align(Alignment.BottomEnd),
                expanded = expanded,
                onToggleClick = { expanded = !expanded },
                onNewConversationClick = { },
                onNewGroupClick = { }
            )
        }
    }
}