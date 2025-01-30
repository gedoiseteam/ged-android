package com.upsaclay.message.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.upsaclay.message.data.model.CONVERSATIONS_TABLE_NAME
import com.upsaclay.message.data.model.ConversationField

@Entity(tableName = CONVERSATIONS_TABLE_NAME)
data class LocalConversation(
    @PrimaryKey
    @ColumnInfo(name = ConversationField.CONVERSATION_ID)
    val conversationId: String,
    @ColumnInfo(name = ConversationField.Local.INTERLOCUTOR)
    val interlocutorJson: String,
    @ColumnInfo(name = ConversationField.CREATED_AT)
    val createdAt: Long,
    @ColumnInfo(name = ConversationField.STATE)
    val state: String
)