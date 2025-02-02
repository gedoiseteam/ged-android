package com.upsaclay.message

import com.upsaclay.common.domain.userFixture
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
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

val conversationFixture = ConversationUI(
    id = "1",
    interlocutor = userFixture2,
    lastMessage = messageFixture.copy(isRead = true),
    createdAt = LocalDateTime.now().minusDays(2),
    state = ConversationState.CREATED
)

val conversationsFixture = listOf(
    conversationFixture,
    conversationFixture.copy(lastMessage = messageFixture.copy(date = messageFixture.date.minusMinutes(1))),
    conversationFixture.copy(lastMessage = messageFixture.copy(date = messageFixture.date.minusMinutes(20))),
    conversationFixture.copy(lastMessage = messageFixture.copy(date = messageFixture.date.minusHours(1))),
    conversationFixture.copy(lastMessage = messageFixture.copy(date = messageFixture.date.minusHours(2))),
    conversationFixture.copy(lastMessage = messageFixture.copy(date = messageFixture.date.minusDays(1))),
    conversationFixture.copy(lastMessage = messageFixture.copy(date = messageFixture.date.minusDays(2))),
    conversationFixture.copy(lastMessage = messageFixture.copy(isRead = true, date = messageFixture.date.minusWeeks(3))),
    conversationFixture.copy(lastMessage = messageFixture.copy(isRead = true, date = messageFixture.date.minusMonths(1)))
)