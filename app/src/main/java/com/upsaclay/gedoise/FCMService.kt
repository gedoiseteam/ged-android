package com.upsaclay.gedoise

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.upsaclay.common.domain.entity.FCMDataType
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.entity.FcmToken
import com.upsaclay.gedoise.domain.usecase.FCMTokenUseCase
import com.upsaclay.gedoise.presentation.NotificationPresenter
import com.upsaclay.message.domain.ConversationMapper
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
    private val fcmTokenUseCase: FCMTokenUseCase by inject<FCMTokenUseCase>()
    private val userRepository: UserRepository by inject<UserRepository>()
    private val scope = GlobalScope

    override fun onNewToken(tokenValue: String) {
        super.onNewToken(tokenValue)
        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            userRepository.currentUser.value?.let {
                fcmTokenUseCase.sendFcmToken(FcmToken(it.id, tokenValue))
            } ?: run {
                fcmTokenUseCase.storeToken(FcmToken(null, tokenValue))
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        scope.launch(Dispatchers.Main) {
           when(remoteMessage.data["type"]) {
               FCMDataType.MESSAGE.toString() -> handleNotification(remoteMessage)
           }
        }
    }

    private suspend fun handleNotification(remoteMessage: RemoteMessage) {
        remoteMessage.data["value"]?.let { value ->
            ConversationMapper.conversationMessageFromJson(value)?.let { conversationMessage ->
                notificationPresenter.showMessageNotification(conversationMessage)
            }
        }
    }
}