package com.upsaclay.gedoise.presentation

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.ImageRepository
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.gedoise.data.ScreenRepository
import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageScreenRoute
import com.upsaclay.message.domain.usecase.GetNewConversationMessageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private const val MESSAGE_CHANNEL_ID = "message_channel_id"

@SuppressLint("MissingPermission")
class NotificationPresenter(
    private val context: Context,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
    private val getNewConversationMessageUseCase: GetNewConversationMessageUseCase,
    private val screenRepository: ScreenRepository
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    val currentUser: StateFlow<User?> = userRepository.currentUser

    fun start() {
        createMessageNotificationChannel()
        listenNewMessages()
    }

    private suspend fun showMessageNotification(conversationMessage: ConversationMessage) {
        if (isCurrentMessageScreen(conversationMessage.conversation.id)) return
        val message = conversationMessage.lastMessage ?: return

        val interlocutor = conversationMessage.conversation.interlocutor
        val intent = makeConversationIntent(conversationMessage.conversation)
        val userIcon = createUserIcon(interlocutor.profilePictureUrl)
        val user = buildPerson(interlocutor, userIcon)

        val notification = buildMessageNotification(
            interlocutor = interlocutor,
            message = message,
            conversationId = conversationMessage.conversation.id,
            person = user,
            intent = intent
        )

        notificationManager.notify(message.id, notification)
    }

    private fun isCurrentMessageScreen(conversationId: Int): Boolean {
        val messageScreen = screenRepository.currentScreenRoute as? MessageScreenRoute.Chat
        return messageScreen?.conversation?.id == conversationId
    }

    private fun createMessageNotificationChannel() {
        val channel = NotificationChannel(
            MESSAGE_CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Message notification"
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun listenNewMessages() {
        GlobalScope.launch(Dispatchers.Main) {
            getNewConversationMessageUseCase()
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collectLatest { conversationsMessage ->
                    conversationsMessage
                        .filter {
                            it.lastMessage?.isSeen() == false &&
                                    it.lastMessage?.senderId != currentUser.value?.id
                        }
                        .forEach { showMessageNotification(it) }
                }
        }
    }

    private fun getCircledBitmap(bitmap: Bitmap): Bitmap {
        val output = createBitmap(bitmap.width, bitmap.height)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(),
            (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(),
            paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    private fun makeConversationIntent(conversation: Conversation): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.CONVERSATION_ID_EXTRA, ConversationMapper.toJson(conversation))
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        return PendingIntent.getActivity(
            context,
            GenerateIdUseCase.asInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildPerson(interlocutor: User, icon: IconCompat): Person {
        return Person.Builder()
            .setIcon(icon)
            .setName(interlocutor.fullName)
            .build()
    }

    private suspend fun createUserIcon(profilePictureUrl: String?): IconCompat {
        val profilePicture = runCatching {
            profilePictureUrl?.let { imageRepository.getImage(it) }
        }.getOrNull()

        return profilePicture?.let {
            IconCompat.createWithBitmap(getCircledBitmap(it))
        } ?: IconCompat.createWithResource(context, com.upsaclay.common.R.drawable.default_profile_picture)
    }

    private fun buildMessageNotification(
        interlocutor: User,
        message: Message,
        conversationId: Int,
        person: Person,
        intent: PendingIntent
    ): Notification {
        val messageKey = ConvertDateUseCase.toTimestamp(message.date).toString()

        val notificationBuilder = NotificationCompat.Builder(context, MESSAGE_CHANNEL_ID)
            .setContentTitle(interlocutor.fullName)
            .setContentText(message.content)
            .setSmallIcon(com.upsaclay.common.R.drawable.ic_stat_name)
            .setGroup(conversationId.toString())
            .setSortKey(messageKey)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(intent)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.MessagingStyle(person)
                    .addMessage(
                        message.content,
                        ConvertDateUseCase.toTimestamp(message.date),
                        person
                    )
            )

        val newGroup = notificationManager.activeNotifications.none { it.notification.group == conversationId.toString() }

        if (newGroup) {
            notificationBuilder.setGroupSummary(true)
        }

        return notificationBuilder.build()
    }
}