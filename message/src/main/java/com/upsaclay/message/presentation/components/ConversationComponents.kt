package com.upsaclay.message.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upsaclay.common.domain.usecase.GetElapsedTimeUseCase
import com.upsaclay.common.domain.usecase.LocalDateTimeFormatterUseCase
import com.upsaclay.common.presentation.components.ProfilePicture
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.userFixture2
import com.upsaclay.message.R
import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.utils.messagesFixture

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationItem(modifier: Modifier = Modifier, conversation: Conversation, onClick: () -> Unit, onLongClick: () -> Unit) {
    val lastMessage = conversation.messages.firstOrNull()
    val getElapsedTimeUseCase = GetElapsedTimeUseCase()
    val localDateTimeFormatterUseCase = LocalDateTimeFormatterUseCase()

    val elapsedTimeValue: String = if (lastMessage != null) {
        val elapsedTime = getElapsedTimeUseCase.fromLocalDateTime(lastMessage.date)

        when (elapsedTime) {
            is com.upsaclay.common.domain.model.ElapsedTime.Now -> stringResource(id = com.upsaclay.common.R.string.now)

            is com.upsaclay.common.domain.model.ElapsedTime.Minute -> stringResource(
                com.upsaclay.common.R.string.minute_ago_short,
                elapsedTime.value
            )

            is com.upsaclay.common.domain.model.ElapsedTime.Hour -> stringResource(
                com.upsaclay.common.R.string.hour_ago_short,
                elapsedTime.value
            )

            is com.upsaclay.common.domain.model.ElapsedTime.Day -> stringResource(
                com.upsaclay.common.R.string.day_ago_short,
                elapsedTime.value
            )

            is com.upsaclay.common.domain.model.ElapsedTime.Week -> stringResource(
                com.upsaclay.common.R.string.week_ago_short,
                elapsedTime.value
            )

            is com.upsaclay.common.domain.model.ElapsedTime.Later -> localDateTimeFormatterUseCase.formatDayMonthYear(elapsedTime.value)
        }
    } else {
        ""
    }

    val unreadMessage = lastMessage?.let {
        it.senderId == conversation.interlocutor.id && !it.isRead
    } ?: false

    Row(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.smallMedium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePicture(
            imageUrl = conversation.interlocutor.profilePictureUrl,
            scale = 0.5f
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.smallMedium))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            if (unreadMessage) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(1f, fill = false),
                            text = conversation.interlocutor.fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.smallMedium))

                        Text(
                            text = elapsedTimeValue,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = lastMessage!!.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                        .size(10.dp)
                )
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(1f, fill = false),
                            text = conversation.interlocutor.fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.smallMedium))

                        Text(
                            text = elapsedTimeValue,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSystemInDarkTheme()) GedoiseColor.surfaceContainerLowDark else GedoiseColor.surfaceContainerLowLight
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    lastMessage?.let {
                        Text(
                            text = lastMessage.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSystemInDarkTheme()) GedoiseColor.surfaceContainerLowDark else GedoiseColor.surfaceContainerLowLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } ?: run {
                        Text(
                            text = stringResource(id = R.string.tap_to_chat),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSystemInDarkTheme()) GedoiseColor.surfaceContainerLowDark else GedoiseColor.surfaceContainerLowLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
private fun ReadConversationItemPreview() {
    val conversation = Conversation(
        id = "1",
        interlocutor = userFixture2,
        messages = messagesFixture
            .map { it.copy(isRead = true, date = it.date.minusDays(2)) }
    )
    GedoiseTheme {
        ConversationItem(
            modifier = Modifier.fillMaxWidth(),
            conversation = conversation,
            onClick = { },
            onLongClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnreadConversationItemPreview() {
    val conversation = Conversation(
        id = "1",
        interlocutor = userFixture2,
        messages = messagesFixture
    )
    GedoiseTheme {
        ConversationItem(
            modifier = Modifier.fillMaxWidth(),
            conversation = conversation,
            onClick = { },
            onLongClick = { }
        )
    }
}