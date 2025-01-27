package com.upsaclay.message.data.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.upsaclay.message.data.model.ConversationField

internal data class RemoteConversation(
    @get:PropertyName(ConversationField.CONVERSATION_ID)
    @set:PropertyName(ConversationField.CONVERSATION_ID)
    var conversationId: String = "",

    @get:PropertyName(ConversationField.Remote.PARTICIPANTS)
    @set:PropertyName(ConversationField.Remote.PARTICIPANTS)
    var participants: List<String> = emptyList(),

    @get:PropertyName(ConversationField.CREATED_AT)
    @set:PropertyName(ConversationField.CREATED_AT)
    var createdAt: Timestamp = Timestamp.now()
)