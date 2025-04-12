package com.upsaclay.message.data.mapper

import com.google.firebase.Timestamp
import com.google.firebase.messaging.RemoteMessage
import com.upsaclay.common.domain.UrlUtils
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.message.data.local.model.LocalConversation
import com.upsaclay.message.data.local.model.LocalConversationMessage
import com.upsaclay.message.data.local.model.LocalMessage
import com.upsaclay.message.data.remote.model.RemoteConversation
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState

internal object ConversationMapper {
    fun toLocal(conversation: Conversation) = LocalConversation(
        conversationId = conversation.id,
        interlocutorId = conversation.interlocutor.id,
        interlocutorFirstName = conversation.interlocutor.firstName,
        interlocutorLastName = conversation.interlocutor.lastName,
        interlocutorEmail = conversation.interlocutor.email,
        interlocutorIsMember = if (conversation.interlocutor.isMember) 1 else 0,
        interlocutorSchoolLevel = conversation.interlocutor.schoolLevel,
        interlocutorProfilePictureFileName = UrlUtils.getFileNameFromUrl(conversation.interlocutor.profilePictureUrl),
        createdAt = ConvertDateUseCase.toTimestamp(conversation.createdAt),
        state = conversation.state.name
    )

    fun toRemote(conversation: Conversation, currentUserId: String) = RemoteConversation(
        conversationId = conversation.id,
        participants = listOf(currentUserId, conversation.interlocutor.id),
        createdAt = Timestamp(ConvertDateUseCase.toInstant(conversation.createdAt))
    )

    fun toConversation(localConversation: LocalConversation): Conversation {
        val interlocutor = User(
            id = localConversation.interlocutorId,
            firstName = localConversation.interlocutorFirstName,
            lastName = localConversation.interlocutorLastName,
            email = localConversation.interlocutorEmail,
            schoolLevel = localConversation.interlocutorSchoolLevel,
            isMember = localConversation.interlocutorIsMember == 1,
            profilePictureUrl = UrlUtils.formatProfilePictureUrl(localConversation.interlocutorProfilePictureFileName)
        )

        return Conversation(
            id = localConversation.conversationId,
            interlocutor = interlocutor,
            createdAt = ConvertDateUseCase.toLocalDateTime(localConversation.createdAt),
            state = ConversationState.valueOf(localConversation.state)
        )
    }

    fun toConversation(remoteConversation: RemoteConversation, interlocutor: User): Conversation {
        return Conversation(
            id = remoteConversation.conversationId,
            interlocutor = interlocutor,
            createdAt = ConvertDateUseCase.toLocalDateTime(remoteConversation.createdAt.toInstant()),
            state = ConversationState.CREATED
        )
    }

    fun toConversationMessage(localConversationMessage: LocalConversationMessage) = ConversationMessage(
        conversation = toConversation(
            LocalConversation(
                conversationId = localConversationMessage.conversationId,
                interlocutorId = localConversationMessage.interlocutorId,
                interlocutorFirstName = localConversationMessage.interlocutorFirstName,
                interlocutorLastName = localConversationMessage.interlocutorLastName,
                interlocutorEmail = localConversationMessage.interlocutorEmail,
                interlocutorSchoolLevel = localConversationMessage.interlocutorSchoolLevel,
                interlocutorIsMember = if (localConversationMessage.interlocutorIsMember) 1 else 0,
                interlocutorProfilePictureFileName = localConversationMessage.interlocutorProfilePictureFileName,
                createdAt = localConversationMessage.createdAt,
                state = localConversationMessage.conversationState
            )
        ),
        lastMessage = if(
            localConversationMessage.messageId == null ||
            localConversationMessage.senderId == null ||
            localConversationMessage.recipientId == null ||
            localConversationMessage.content == null ||
            localConversationMessage.messageTimestamp == null ||
            localConversationMessage.messageState == null
        ) null else MessageMapper.toDomain(
            LocalMessage(
                messageId = localConversationMessage.messageId,
                conversationId = localConversationMessage.conversationId,
                senderId = localConversationMessage.senderId,
                recipientId = localConversationMessage.recipientId,
                content = localConversationMessage.content,
                messageTimestamp = localConversationMessage.messageTimestamp,
                seenValue = localConversationMessage.seenValue,
                seenTimestamp = localConversationMessage.seenTimestamp,
                state = localConversationMessage.messageState
            )
        )
    )
}