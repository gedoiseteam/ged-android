package com.upsaclay.message.domain

import com.upsaclay.common.domain.userFixture
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.entity.Seen
import java.time.LocalDateTime

val messageFixture = Message(
    id = 1,
    senderId = userFixture.id,
    conversationId = 1,
    content = "Salut, bien et toi ? Oui bien sûr.",
    date = LocalDateTime.of(2024, 7, 20, 10, 0),
    seen = Seen(),
    state = MessageState.SENT
)

val messageFixture2 = Message(
    id = 2,
    senderId = userFixture2.id,
    conversationId = 1,
    content = "Salut ça va ? Cela fait longtemps que j'attend de te parler. Pourrait-on se voir ?",
    date = LocalDateTime.now(),
    seen = null,
    state = MessageState.SENT
)

val messagesFixture = listOf(
    messageFixture.copy(id = 1, seen = Seen()),
    messageFixture2.copy(id = 2, date = LocalDateTime.now().minusDays(2), seen = Seen()),
    messageFixture.copy(id = 3, date = LocalDateTime.now().minusDays(1), seen = Seen()),
    messageFixture2.copy(id = 4, date = LocalDateTime.now(), seen = Seen()),
    messageFixture2.copy(id = 5, date = LocalDateTime.now(), seen = Seen()),
    messageFixture2.copy(id = 6, date = LocalDateTime.now(), seen = Seen()),
    messageFixture2.copy(id = 7, date = LocalDateTime.now(), seen = Seen()),
    messageFixture2.copy(id = 8, date = LocalDateTime.now(), seen = Seen()),
    messageFixture2.copy(id = 9, date = LocalDateTime.now(), seen = Seen()),
)

val conversationUIFixture = ConversationUI(
    id = 1,
    interlocutor = userFixture2,
    lastMessage = messageFixture,
    createdAt = LocalDateTime.of(2024, 7, 20, 10, 0),
    state = ConversationState.CREATED
)

val conversationFixture = Conversation(
    id = 1,
    interlocutor = userFixture2,
    createdAt = LocalDateTime.of(2024, 7, 20, 10, 0),
    state = ConversationState.CREATED
)

val conversationMessageFixture = ConversationMessage(
   conversation = conversationFixture,
    lastMessage = messageFixture
)

val conversationsUIFixture = listOf(
    conversationUIFixture,
    conversationUIFixture.copy(id = 2, lastMessage = messageFixture.copy(date = messageFixture.date.minusMinutes(1))),
    conversationUIFixture.copy(id = 3, lastMessage = messageFixture.copy(date = messageFixture.date.minusMinutes(20))),
    conversationUIFixture.copy(id = 4, lastMessage = messageFixture.copy(date = messageFixture.date.minusHours(1))),
    conversationUIFixture.copy(id = 5, lastMessage = messageFixture.copy(date = messageFixture.date.minusHours(2))),
    conversationUIFixture.copy(id = 6, lastMessage = messageFixture.copy(date = messageFixture.date.minusDays(1))),
    conversationUIFixture.copy(id = 7, lastMessage = messageFixture.copy(date = messageFixture.date.minusDays(2))),
    conversationUIFixture.copy(id = 8, lastMessage = messageFixture.copy(seen = Seen(), date = messageFixture.date.minusWeeks(3))),
    conversationUIFixture.copy(id = 9, lastMessage = messageFixture.copy(seen = Seen(), date = messageFixture.date.minusMonths(1)))
)

val conversationsFixture = listOf(
    conversationFixture,
    conversationFixture.copy(createdAt = LocalDateTime.now().minusMinutes(1)),
    conversationFixture.copy(createdAt = LocalDateTime.now().minusMinutes(20)),
    conversationFixture.copy(createdAt = LocalDateTime.now().minusHours(1)),
    conversationFixture.copy(createdAt = LocalDateTime.now().minusHours(2)),
    conversationFixture.copy(createdAt = LocalDateTime.now().minusDays(1)),
    conversationFixture.copy(createdAt = LocalDateTime.now().minusDays(2)),
    conversationFixture.copy(createdAt = LocalDateTime.now().minusWeeks(3)),
    conversationFixture.copy(createdAt = LocalDateTime.now().minusMonths(1))
)

val conversationsMessageFixture = listOf(
    conversationMessageFixture
)