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
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.gedoise.presentation.viewmodels.NavigationViewModel
import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.MessageScreen
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {
    private val navigationViewModel: NavigationViewModel by viewModel()
    private val notificationPresenter: NotificationPresenter by inject<NotificationPresenter>()
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            notificationPresenter.start()
        }
    }

    companion object {
        const val CONVERSATION_ID_EXTRA = "conversation_id_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startNotification()

        setContent {
            GedoiseTheme {
                 Navigation(navigationViewModel)
            }
        }
    }

    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
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
        intent.getStringExtra(CONVERSATION_ID_EXTRA)?.let {
            val conversation = ConversationMapper.fromJson(it)
            navigationViewModel.navigateTo(MessageScreen.Chat(conversation))
        }
    }
}