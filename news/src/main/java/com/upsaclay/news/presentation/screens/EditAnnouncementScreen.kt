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
import androidx.compose.material3.Snackbar
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
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.presentation.components.SmallTopBarAction
import com.upsaclay.common.presentation.components.TopLinearLoadingScreen
import com.upsaclay.common.presentation.components.TransparentFocusedTextField
import com.upsaclay.common.presentation.components.TransparentTextField
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.news.R
import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.domain.entity.AnnouncementEvent
import com.upsaclay.news.presentation.viewmodels.EditAnnouncementViewModel
import kotlinx.coroutines.flow.collectLatest
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
    var loading by remember { mutableStateOf(false) }

    val isAnnouncementModified by editAnnouncementViewModel.isAnnouncementModified.collectAsState()
    val announcement by editAnnouncementViewModel.announcement.collectAsState()
    val title by editAnnouncementViewModel.title.collectAsState()
    val content by editAnnouncementViewModel.content.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackBar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    LaunchedEffect(Unit) {
        editAnnouncementViewModel.event.collectLatest {
            loading = it is AnnouncementEvent.Loading
            when (it) {
                is AnnouncementEvent.Updated -> navController.popBackStack()

                is AnnouncementEvent.Error -> {
                    when(it.type) {
                        is ErrorType.NetworkError -> showSnackBar (context.getString(com.upsaclay.common.R.string.unknown_network_error))
                        is ErrorType.UnknownError -> showSnackBar(context.getString(R.string.announcement_update_error))
                    }
                }

                else -> Unit
            }
        }
    }

    if (loading) {
        TopLinearLoadingScreen()
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
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    announcement?.let {
                        editAnnouncementViewModel.updateAnnouncement(
                            it.copy(
                                title = title,
                                content = content
                            )
                        )
                    } ?: showSnackBar(context.getString(R.string.announcement_update_error))
                },
                isButtonEnable = content.isNotBlank() && isAnnouncementModified && !loading,
                buttonText = stringResource(id = com.upsaclay.common.R.string.save)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    modifier = Modifier.testTag(stringResource(R.string.edit_screen_snackbar_tag)),
                    snackbarData = it
                )
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
                    value = title ?: "",
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
                    ),
                    enabled = !loading
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

                TransparentTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = content,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.content_field_entry),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onValueChange = { editAnnouncementViewModel.updateContent(it) },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    enabled = !loading
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