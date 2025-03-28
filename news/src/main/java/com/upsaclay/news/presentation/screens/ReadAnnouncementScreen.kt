package com.upsaclay.news.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.presentation.components.ClickableItem
import com.upsaclay.common.presentation.components.LinearProgressBar
import com.upsaclay.common.presentation.components.OverlayLinearLoadingScreen
import com.upsaclay.common.presentation.components.SensibleActionDialog
import com.upsaclay.common.presentation.components.SmallTopBarBack
import com.upsaclay.common.presentation.components.TopLinearLoadingScreen
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.news.R
import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementEvent
import com.upsaclay.news.domain.entity.NewsScreenRoute
import com.upsaclay.news.presentation.components.AnnouncementHeader
import com.upsaclay.news.presentation.viewmodels.ReadAnnouncementViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadAnnouncementScreen(
    announcementId: String,
    modifier: Modifier = Modifier,
    navController: NavController,
    readAnnouncementViewModel: ReadAnnouncementViewModel = koinViewModel(
        parameters = { parametersOf(announcementId) }
    )
) {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteAnnouncementDialog by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val user by readAnnouncementViewModel.currentUser.collectAsState()
    val announcement by readAnnouncementViewModel.announcement.collectAsState()

    val hideBottomSheet = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
            }
        }
    }

    val showSnackBar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    LaunchedEffect(Unit) {
        readAnnouncementViewModel.event.collectLatest {
            loading = it is AnnouncementEvent.Loading
            when (it) {
                is AnnouncementEvent.Deleted -> navController.popBackStack()

                is AnnouncementEvent.Error -> {
                    when(it.type) {
                        is ErrorType.NetworkError -> showSnackBar(context.getString(com.upsaclay.common.R.string.unknown_network_error))
                        is ErrorType.UnknownError -> showSnackBar(context.getString(R.string.announcement_delete_error))
                    }
                }

                else -> Unit
            }
        }
    }

    if (showDeleteAnnouncementDialog) {
        SensibleActionDialog(
            modifier = Modifier.testTag(stringResource(id = R.string.read_screen_delete_dialog_tag)),
            title = stringResource(id = R.string.delete_announcement_dialog_title),
            text = stringResource(id = R.string.delete_announcement_dialog_text),
            confirmText = stringResource(id = com.upsaclay.common.R.string.delete),
            onConfirm = {
                showDeleteAnnouncementDialog = false
                readAnnouncementViewModel.deleteAnnouncement()
            },
            onCancel = { showDeleteAnnouncementDialog = false }
        )
    }

    if (loading) {
        OverlayLinearLoadingScreen()
    }

    Scaffold(
        topBar = {
            SmallTopBarBack(
                onBackClick = { navController.popBackStack() },
                title = stringResource(id = R.string.announcement)
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
            ) {
               Snackbar(
                   snackbarData = it,
                   modifier = Modifier.testTag(stringResource(id = R.string.read_screen_snackbar_tag))
               )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.medium
                )
                .verticalScroll(rememberScrollState())
        ) {
            if (user?.isMember == true && announcement?.author?.id == user?.id) {
                announcement?.let {
                    EditableTopSection(
                        announcement = it,
                        onEditClick = { showBottomSheet = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            announcement?.title?.let {
                Text(
                    modifier = Modifier.testTag(stringResource(id = R.string.read_screen_announcement_title_tag)),
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))
            }

            announcement?.content?.let {
                Text(
                    modifier = Modifier.testTag(stringResource(id = R.string.read_screen_announcement_content_tag)),
                    text = it,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    modifier = Modifier.testTag(stringResource(id = R.string.read_screen_bottom_sheet_tag)),
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                ) {
                    ClickableItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(stringResource(id = R.string.read_screen_sheet_edit_field_tag)),
                        text = { Text(text = stringResource(id = R.string.edit_announcement)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            navController.navigate(NewsScreenRoute.EditAnnouncement(announcementId).route)
                            hideBottomSheet()
                        }
                    )

                    ClickableItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(stringResource(id = R.string.read_screen_sheet_delete_field_tag)),
                        text = {
                            Text(
                                text = stringResource(id = com.upsaclay.common.R.string.delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            hideBottomSheet()
                            showDeleteAnnouncementDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
                }
            }
        }
    }
}

@Composable
private fun EditableTopSection(
    announcement: Announcement,
    onEditClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AnnouncementHeader(
            announcement = announcement,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.smallMedium))

        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .testTag(stringResource(id = R.string.read_screen_option_button_tag))
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.MoreVert,
                tint = Color.Gray,
                contentDescription = stringResource(id = R.string.announcement_item_more_vert_description)
            )
        }
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(showBackground = true)
@Composable
private fun ReadOnlyAnnouncementScreenPreview() {
    val announcement = announcementFixture

    GedoiseTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.medium)
        ) {
            AnnouncementHeader(announcement = announcement)

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            announcement.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditableAnnouncementScreenPreview() {
    val announcement = announcementFixture

    GedoiseTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.medium)
        ) {
            EditableTopSection(
                announcement = announcement,
                onEditClick = {}
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            announcement.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyLarge

            )
        }
    }
}