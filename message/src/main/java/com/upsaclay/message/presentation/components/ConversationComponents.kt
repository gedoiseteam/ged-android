package com.upsaclay.message.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.upsaclay.common.domain.model.ElapsedTime
import com.upsaclay.common.domain.usecase.GetElapsedTimeUseCase
import com.upsaclay.common.domain.usecase.LocalDateTimeFormatterUseCase
import com.upsaclay.common.presentation.components.ProfilePicture
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.R
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.conversationFixture

@Composable
fun ConversationItem(
    modifier: Modifier = Modifier,
    conversation: ConversationUI,
    onClick: () -> Unit
) {
    val lastMessage = conversation.lastMessage

    val elapsedTimeValue: String = if (lastMessage != null) {
        val elapsedTime = GetElapsedTimeUseCase.fromLocalDateTime(lastMessage.date)

        when (elapsedTime) {
            is ElapsedTime.Now -> stringResource(id = com.upsaclay.common.R.string.now)

            is ElapsedTime.Minute -> stringResource(
                com.upsaclay.common.R.string.minute_ago_short,
                elapsedTime.value
            )

            is ElapsedTime.Hour -> stringResource(
                com.upsaclay.common.R.string.hour_ago_short,
                elapsedTime.value
            )

            is ElapsedTime.Day -> stringResource(
                com.upsaclay.common.R.string.day_ago_short,
                elapsedTime.value
            )

            is ElapsedTime.Week -> stringResource(
                com.upsaclay.common.R.string.week_ago_short,
                elapsedTime.value
            )

            is ElapsedTime.Later -> LocalDateTimeFormatterUseCase.formatDayMonthYear(elapsedTime.value)
        }
    } else {
        ""
    }

    val readMessage = lastMessage?.let {
        it.senderId == conversation.interlocutor.id && it.isRead
    } ?: false

    Row(
        modifier = modifier
            .clickable(onClick = onClick)
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
            if (!readMessage) {
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
                            text = lastMessage!!.content,
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
                            color = GedoiseColor.PreviewText
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    lastMessage?.let {
                        Text(
                            text = lastMessage.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GedoiseColor.PreviewText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } ?: run {
                        Text(
                            text = stringResource(id = R.string.tap_to_chat),
                            style = MaterialTheme.typography.bodyMedium,
                            color = GedoiseColor.PreviewText,
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
    GedoiseTheme {
        ConversationItem(
            modifier = Modifier.fillMaxWidth(),
            conversation = conversationFixture,
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnreadConversationItemPreview() {
    GedoiseTheme {
        ConversationItem(
            modifier = Modifier.fillMaxWidth(),
            conversation = conversationFixture.copy(lastMessage = conversationFixture.lastMessage!!.copy(isRead = false)),
            onClick = { }
        )
    }
}