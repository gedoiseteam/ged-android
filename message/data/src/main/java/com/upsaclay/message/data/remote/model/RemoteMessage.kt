package com.upsaclay.message.data.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.upsaclay.message.data.model.MessageField

internal data class RemoteMessage(
    @get:PropertyName(MessageField.MESSAGE_ID)
    @set:PropertyName(MessageField.MESSAGE_ID)
    var messageId: Int = 0,

    @get:PropertyName(MessageField.CONVERSATION_ID)
    @set:PropertyName(MessageField.CONVERSATION_ID)
    var conversationId: Int = 0,

    @get:PropertyName(MessageField.SENDER_ID)
    @set:PropertyName(MessageField.SENDER_ID)
    var senderId: String = "",

    @get:PropertyName(MessageField.RECIPIENT_ID)
    @set:PropertyName(MessageField.RECIPIENT_ID)
    var recipientId: String = "",

    @get:PropertyName(MessageField.CONTENT)
    @set:PropertyName(MessageField.CONTENT)
    var content: String = "",

    @get:PropertyName(MessageField.MESSAGE_TIMESTAMP)
    @set:PropertyName(MessageField.MESSAGE_TIMESTAMP)
    var timestamp: Timestamp = Timestamp.now(),

    @get:PropertyName(MessageField.Remote.SEEN)
    @set:PropertyName(MessageField.Remote.SEEN)
    var seen: RemoteSeen? = null
)

internal data class RemoteSeen(
    @get:PropertyName(MessageField.Remote.SEEN_VALUE)
    @set:PropertyName(MessageField.Remote.SEEN_VALUE)
    var value: Boolean = true,

    @get:PropertyName(MessageField.Remote.SEEN_TIME)
    @set:PropertyName(MessageField.Remote.SEEN_TIME)
    var time: Timestamp = Timestamp.now(),
)