package com.upsaclay.message.domain

import com.upsaclay.common.domain.userFixture
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import java.time.LocalDateTime

val messageFixture = Message(
    id = "1",
    senderId = userFixture.id,
    conversationId = "1",
    content = "Salut, bien et toi ? Oui bien sûr.",
    date = LocalDateTime.of(2024, 7, 20, 10, 0),
    isRead = true,
    state = MessageState.SENT,
    type = "text"
)

val messageFixture2 = Message(
    id = "2",
    senderId = userFixture2.id,
    conversationId = "1",
    content = "Salut ça va ? Cela fait longtemps que j'attend de te parler. Pourrait-on se voir ?",
    date = LocalDateTime.now(),
    isRead = false,
    state = MessageState.SENT,
    type = "text"
)

val messagesFixture = listOf(
    messageFixture,
    messageFixture2,
    messageFixture.copy(id = "2", date = LocalDateTime.now())
)

val conversationUIFixture = ConversationUI(
    id = "1",
    interlocutor = userFixture2,
    lastMessage = messageFixture,
    createdAt = LocalDateTime.of(2024, 7, 20, 10, 0),
    state = ConversationState.CREATED
)

val conversationUserFixture = ConversationUser(
    id = "1",
    interlocutor = userFixture2,
    createdAt = LocalDateTime.of(2024, 7, 20, 10, 0),
    state = ConversationState.CREATED
)

val conversationsUIFixture = listOf(
    conversationUIFixture,
    conversationUIFixture.copy(id = "2", lastMessage = messageFixture.copy(date = messageFixture.date.minusMinutes(1))),
    conversationUIFixture.copy(id = "3", lastMessage = messageFixture.copy(date = messageFixture.date.minusMinutes(20))),
    conversationUIFixture.copy(id = "4", lastMessage = messageFixture.copy(date = messageFixture.date.minusHours(1))),
    conversationUIFixture.copy(id = "5", lastMessage = messageFixture.copy(date = messageFixture.date.minusHours(2))),
    conversationUIFixture.copy(id = "6", lastMessage = messageFixture.copy(date = messageFixture.date.minusDays(1))),
    conversationUIFixture.copy(id = "7", lastMessage = messageFixture.copy(date = messageFixture.date.minusDays(2))),
    conversationUIFixture.copy(id = "8", lastMessage = messageFixture.copy(isRead = true, date = messageFixture.date.minusWeeks(3))),
    conversationUIFixture.copy(id = "9", lastMessage = messageFixture.copy(isRead = true, date = messageFixture.date.minusMonths(1)))
)

val conversationsUserFixture = listOf(
    conversationUserFixture,
    conversationUserFixture.copy(createdAt = LocalDateTime.now().minusMinutes(1)),
    conversationUserFixture.copy(createdAt = LocalDateTime.now().minusMinutes(20)),
    conversationUserFixture.copy(createdAt = LocalDateTime.now().minusHours(1)),
    conversationUserFixture.copy(createdAt = LocalDateTime.now().minusHours(2)),
    conversationUserFixture.copy(createdAt = LocalDateTime.now().minusDays(1)),
    conversationUserFixture.copy(createdAt = LocalDateTime.now().minusDays(2)),
    conversationUserFixture.copy(createdAt = LocalDateTime.now().minusWeeks(3)),
    conversationUserFixture.copy(createdAt = LocalDateTime.now().minusMonths(1))
)