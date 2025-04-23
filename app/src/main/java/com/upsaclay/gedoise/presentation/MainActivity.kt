package com.upsaclay.gedoise.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.upsaclay.common.domain.entity.FCMDataType
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.gedoise.presentation.viewmodels.MainViewModel
import com.upsaclay.gedoise.presentation.viewmodels.NavigationViewModel
import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.MessageScreenRoute
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    private val navigationViewModel: NavigationViewModel by viewModel()
    private val notificationPresenter: NotificationPresenter by inject<NotificationPresenter>()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                notificationPresenter.start()
            }
        }

    companion object {
        const val CONVERSATION_ID_EXTRA = "conversation_id_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.startListening()
        startNotification()

        setContent {
            GedoiseTheme {
                Navigation(navigationViewModel)
            }
        }

        intent?.let {
            handleIntent(it)
        }
    }

    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationPresenter.start()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            notificationPresenter.start()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val conversationIdExtra = intent.getStringExtra(CONVERSATION_ID_EXTRA)
        val notificationTypeExtra = intent.getStringExtra("type")

        when {
            conversationIdExtra != null -> {
                ConversationMapper.conversationFromJson(conversationIdExtra)?.let { conversation ->
                    navigationViewModel.updateIntentScreenNavigate(
                        MessageScreenRoute.Chat(conversation)
                    )
                }
            }

            notificationTypeExtra != null -> handleNotificationIntent(notificationTypeExtra, intent.extras)
        }
    }

    private fun handleNotificationIntent(type: String, extras: Bundle?) {
        when (type) {
            FCMDataType.MESSAGE.toString() -> {
                extras?.getString("value")?.let {
                    ConversationMapper.conversationMessageFromJson(it)?.let { conversationMessage ->
                        navigationViewModel.updateIntentScreenNavigate(
                            MessageScreenRoute.Chat(conversationMessage.conversation)
                        )
                    }
                }
            }

            else -> Unit
        }
    }
}
