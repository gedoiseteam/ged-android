package com.upsaclay.gedoise

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.UrlUtils.formatProfilePictureUrl
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.gedoise.domain.entities.FcmToken
import com.upsaclay.gedoise.domain.repository.CredentialsRepository
import com.upsaclay.gedoise.presentation.NotificationPresenter
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FCMService: FirebaseMessagingService() {
    private var job: Job? = null
    private val notificationPresenter: NotificationPresenter by inject<NotificationPresenter>()
    private val credentialsRepository: CredentialsRepository by inject<CredentialsRepository>()
    private val userRepository: UserRepository by inject<UserRepository>()
    private val scope = GlobalScope

    override fun onNewToken(tokenValue: String) {
        super.onNewToken(tokenValue)
        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            val user = userRepository.currentUser.filterNotNull().first()
            val fcmToken = FcmToken(user.id, tokenValue)

            runCatching {
                credentialsRepository.sendFcmToken(fcmToken)
                credentialsRepository.removeUnsentFcmToken()
            }
                .onFailure {
                    e("Error sending FCM token", it)
                    credentialsRepository.storeUnsentFcmToken(fcmToken)
                }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        scope.launch(Dispatchers.Main) {
            notificationPresenter.showMessageNotification(getConversationMessage(remoteMessage))
        }
    }

    private fun getConversationMessage(remoteMessage: RemoteMessage): ConversationMessage {
        return ConversationMessage(
            conversation = Conversation(
                id = remoteMessage.data["conversationId"]?.toInt() ?: 0,
                interlocutor = User(
                    id = remoteMessage.data["interlocutorId"] ?: "",
                    firstName = remoteMessage.data["interlocutorFirstName"] ?: "",
                    lastName = remoteMessage.data["interlocutorLastName"] ?: "",
                    email = remoteMessage.data["interlocutorEmail"] ?: "",
                    schoolLevel = remoteMessage.data["interlocutorSchoolLevel"] ?: "",
                    isMember = remoteMessage.data["interlocutorIsMember"]?.toBoolean() ?: false,
                    profilePictureUrl = formatProfilePictureUrl(remoteMessage.data["interlocutorProfilePictureFileName"] ?: "")
                ),
                createdAt = ConvertDateUseCase.toLocalDateTime(
                    remoteMessage.data["conversationCreatedAt"]?.toLongOrNull() ?: 0L
                ),
                state = ConversationState.CREATED
            ),
            lastMessage = Message(
                id = remoteMessage.data["messageId"]?.toInt() ?: 0,
                senderId = remoteMessage.data["senderId"] ?: "",
                recipientId = remoteMessage.data["recipientId"] ?: "",
                conversationId = remoteMessage.data["conversationId"]?.toInt() ?: 0,
                content = remoteMessage.data["messageContent"] ?: "",
                date = ConvertDateUseCase.toLocalDateTime(
                    remoteMessage.data["messageDate"]?.toLongOrNull() ?: 0L
                ),
                seen = null,
                state = MessageState.SENT
            )
        )
    }
}