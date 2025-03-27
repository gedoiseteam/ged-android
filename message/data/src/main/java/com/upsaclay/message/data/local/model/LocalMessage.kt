package com.upsaclay.message.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.upsaclay.message.data.model.MESSAGES_TABLE_NAME
import com.upsaclay.message.data.model.MessageField

@Entity(tableName = MESSAGES_TABLE_NAME)
data class LocalMessage(
    @PrimaryKey
    @ColumnInfo(name = MessageField.MESSAGE_ID)
    val messageId: Int,
    @ColumnInfo(name = MessageField.SENDER_ID)
    val senderId: String,
    @ColumnInfo(name = MessageField.CONVERSATION_ID)
    val conversationId: Int,
    @ColumnInfo(name = MessageField.CONTENT)
    val content: String,
    @ColumnInfo(name = MessageField.MESSAGE_TIMESTAMP)
    val messageTimestamp: Long,
    @ColumnInfo(name = MessageField.Local.SEEN_VALUE)
    val seenValue: Boolean?,
    @ColumnInfo(name = MessageField.Local.SEEN_TIMESTAMP)
    val seenTimestamp: Long?,
    @ColumnInfo(name = MessageField.MESSAGE_STATE)
    val state: String
)