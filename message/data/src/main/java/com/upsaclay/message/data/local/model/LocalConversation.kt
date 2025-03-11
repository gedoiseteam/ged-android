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
    val conversationId: Int,
    @ColumnInfo(name = ConversationField.Local.INTERLOCUTOR_ID)
    val interlocutorId: String,
    @ColumnInfo(name = ConversationField.Local.INTERLOCUTOR_FIRST_NAME)
    val interlocutorFirstName: String,
    @ColumnInfo(name = ConversationField.Local.INTERLOCUTOR_LAST_NAME)
    val interlocutorLastName: String,
    @ColumnInfo(name = ConversationField.Local.INTERLOCUTOR_EMAIL)
    val interlocutorEmail: String,
    @ColumnInfo(name = ConversationField.Local.INTERLOCUTOR_SCHOOL_LEVEL)
    val interlocutorSchoolLevel: String,
    @ColumnInfo(name = ConversationField.Local.INTERLOCUTOR_IS_MEMBER)
    val interlocutorIsMember: Int,
    @ColumnInfo(name = ConversationField.Local.INTERLOCUTOR_PROFILE_PICTURE_FILE_NAME)
    val interlocutorProfilePictureFileName: String?,
    @ColumnInfo(name = ConversationField.CREATED_AT)
    val createdAt: Long,
    @ColumnInfo(name = ConversationField.CONVERSATION_STATE)
    val state: String
)