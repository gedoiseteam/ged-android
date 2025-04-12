package com.upsaclay.gedoise

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.upsaclay.common.domain.UrlUtils.formatProfilePictureUrl
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.common.domain.entity.FcmToken
import com.upsaclay.common.domain.repository.FCMRepository
import com.upsaclay.gedoise.presentation.NotificationPresenter
import com.upsaclay.message.domain.ConversationMapper
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
    private val fcmRepository: FCMRepository by inject<FCMRepository>()
    private val userRepository: UserRepository by inject<UserRepository>()
    private val scope = GlobalScope

    override fun onNewToken(tokenValue: String) {
        super.onNewToken(tokenValue)
        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            val user = userRepository.currentUser.filterNotNull().first()
            val fcmToken = FcmToken(user.id, tokenValue)

            runCatching {
                fcmRepository.sendFcmToken(fcmToken)
                fcmRepository.removeUnsentFcmToken()
            }
                .onFailure {
                    e("Error sending FCM token", it)
                    fcmRepository.storeUnsentFcmToken(fcmToken)
                }
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        scope.launch(Dispatchers.Main) {
            notificationPresenter.showMessageNotification(ConversationMapper.fromFcmFormat(remoteMessage.data))
        }
    }
}