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
    val messageId: String,
    @ColumnInfo(name = MessageField.SENDER_ID)
    val senderId: String,
    @ColumnInfo(name = MessageField.CONVERSATION_ID)
    val conversationId: String,
    @ColumnInfo(name = MessageField.CONTENT)
    val content: String,
    @ColumnInfo(name = MessageField.TIMESTAMP)
    val timestamp: Long,
    @ColumnInfo(name = MessageField.IS_READ)
    val isRead: Boolean,
    @ColumnInfo(name = MessageField.STATE)
    val state: String,
    @ColumnInfo(name = MessageField.TYPE)
    val type: String
)