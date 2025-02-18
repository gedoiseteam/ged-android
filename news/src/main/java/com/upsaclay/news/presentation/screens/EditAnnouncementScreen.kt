package com.upsaclay.news.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.upsaclay.common.domain.entity.SnackbarType
import com.upsaclay.common.presentation.components.ErrorSnackBar
import com.upsaclay.common.presentation.components.InfoSnackbar
import com.upsaclay.common.presentation.components.LinearProgressBar
import com.upsaclay.common.presentation.components.SmallTopBarAction
import com.upsaclay.common.presentation.components.TransparentFocusedTextField
import com.upsaclay.common.presentation.components.TransparentTextField
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.news.R
import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.presentation.viewmodels.EditAnnouncementViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditAnnouncementScreen(
    announcementId: String,
    navController: NavController,
    editAnnouncementViewModel: EditAnnouncementViewModel = koinViewModel(
        parameters = { parametersOf(announcementId) }
    )
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val state by editAnnouncementViewModel.screenState.collectAsState()
    val isAnnouncementModified by editAnnouncementViewModel.isAnnouncementModified.collectAsState()
    val announcement by editAnnouncementViewModel.announcement.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackBarType by remember { mutableStateOf(SnackbarType.INFO) }
    val showSnackBar = { type: SnackbarType, message: String ->
        snackBarType = type
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    val resetScreenState = {
        editAnnouncementViewModel.resetScreenState()
    }

    LaunchedEffect(state) {
        when (state) {
            AnnouncementScreenState.UPDATED -> {
                isLoading = false
                focusManager.clearFocus()
                navController.popBackStack()
            }

            AnnouncementScreenState.LOADING -> {
                keyboardController?.hide()
                isLoading = true
            }

            AnnouncementScreenState.CONNECTION_ERROR -> {
                isLoading = false
                showSnackBar(SnackbarType.ERROR, context.getString(com.upsaclay.common.R.string.server_connection_error))
                resetScreenState()
            }

            AnnouncementScreenState.ERROR -> {
                isLoading = false
                showSnackBar(SnackbarType.ERROR, context.getString(R.string.announcement_update_error))
                resetScreenState()
            }

            else -> {}
        }
    }

    if (isLoading) {
        LinearProgressBar(modifier = Modifier.fillMaxWidth())
    }

    Scaffold(
        topBar = {
            SmallTopBarAction(
                onCancelClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    navController.popBackStack()
                },
                onActionClick = {
                    announcement?.let {
                        editAnnouncementViewModel.updateAnnouncement(
                            it.copy(
                                title = editAnnouncementViewModel.title,
                                content = editAnnouncementViewModel.content
                            )
                        )
                    } ?: run {
                        showSnackBar(SnackbarType.ERROR, context.getString(R.string.announcement_update_error))
                        navController.popBackStack()
                    }
                },
                isButtonEnable = editAnnouncementViewModel.content.isNotBlank() && isAnnouncementModified,
                buttonText = stringResource(id = com.upsaclay.common.R.string.save)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                if (snackBarType == SnackbarType.ERROR) {
                    ErrorSnackBar(
                        modifier = Modifier.testTag(stringResource(R.string.edit_screen_error_snackbar_tag)),
                        message = it.visuals.message
                    )
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.medium
                )
                .fillMaxSize()
        ) {
            Column {
                TransparentFocusedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = editAnnouncementViewModel.title,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.title_field_entry),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                        )
                    },
                    onValueChange = { editAnnouncementViewModel.updateTitle(it) },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                    )
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

                TransparentTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = editAnnouncementViewModel.content,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.content_field_entry),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onValueChange = { editAnnouncementViewModel.updateContent(it) },
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview
@Composable
private fun EditAnnouncementScreenPreview() {
    var title by remember { mutableStateOf(announcementFixture.title) }
    var content by remember { mutableStateOf(announcementFixture.content) }

    GedoiseTheme {
        Scaffold(
            topBar = {
                SmallTopBarAction(
                    onCancelClick = { },
                    onActionClick = { },
                    buttonText = stringResource(id = com.upsaclay.common.R.string.save)
                )
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(
                        top = contentPadding.calculateTopPadding(),
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        bottom = MaterialTheme.spacing.medium
                    )
                    .fillMaxSize()
            ) {
                TransparentFocusedTextField(
                    value = title ?: "",
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.title_field_entry),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                        )
                    },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

                TransparentTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.content_field_entry),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}