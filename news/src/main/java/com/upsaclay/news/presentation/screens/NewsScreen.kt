package com.upsaclay.news.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.upsaclay.common.presentation.components.ClickableItem
import com.upsaclay.common.presentation.components.PullToRefreshComponent
import com.upsaclay.common.presentation.components.SensibleActionDialog
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.previewText
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.news.R
import com.upsaclay.news.domain.announcementsFixture
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.entity.NewsScreenRoute
import com.upsaclay.news.presentation.components.AnnouncementItem
import com.upsaclay.news.presentation.viewmodels.NewsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    navController: NavController,
    newsViewModel: NewsViewModel = koinViewModel()
) {
    val announcements by newsViewModel.announcements.collectAsState(emptyList())
    var refreshing by remember { mutableStateOf(false) }
    var showDraftAnnouncementBottomSheet by remember { mutableStateOf(false) }
    var showDeleteAnnouncementDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var announcementClicked by remember { mutableStateOf<Announcement?>(null) }

    val hideBottomSheet = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showDraftAnnouncementBottomSheet = false
            }
        }
    }

    LaunchedEffect(UInt) {
        newsViewModel.refreshing.collect {
            refreshing = it
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
                announcementClicked?.let { newsViewModel.deleteAnnouncement(it) }
            },
            onCancel = { showDeleteAnnouncementDialog = false }
        )
    }

    PullToRefreshComponent(
        onRefresh = { newsViewModel.refreshAnnouncements() },
        isRefreshing = refreshing
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallMedium)
        ) {
            RecentAnnouncementSection(
                announcements = announcements,
                onClickAnnouncement = {
                    navController.navigate(NewsScreenRoute.ReadAnnouncement(it.id).route)
                },
                onClickNotSentAnnouncement = {
                    announcementClicked = it
                    showDraftAnnouncementBottomSheet = true
                }
            )
        }
    }

    if (showDraftAnnouncementBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.testTag(stringResource(id = R.string.read_screen_bottom_sheet_tag)),
            onDismissRequest = { showDraftAnnouncementBottomSheet = false },
            sheetState = sheetState,
        ) {
            ClickableItem(
                modifier = Modifier
                    .fillMaxWidth(),
                text = { Text(text = stringResource(id = R.string.resend_announcement)) },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null
                    )
                },
                onClick = {
                    announcementClicked?.let { newsViewModel.recreateAnnouncement(it) }
                    hideBottomSheet()
                }
            )

            ClickableItem(
                modifier = Modifier.fillMaxWidth(),
                text = {
                    Text(
                        text = stringResource(id = com.upsaclay.common.R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null
                    )
                },
                onClick = {
                    hideBottomSheet()
                    showDeleteAnnouncementDialog = true
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        }
    }
}

@Composable
private fun RecentAnnouncementSection(
    modifier: Modifier = Modifier,
    announcements: List<Announcement>,
    onClickAnnouncement: (Announcement) -> Unit,
    onClickNotSentAnnouncement: (Announcement) -> Unit
) {
    val sortedAnnouncements = announcements.sortedByDescending { it.date }

    Text(
        text = stringResource(id = R.string.recent_announcements),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(horizontal = MaterialTheme.spacing.medium)
            .testTag(stringResource(id = R.string.news_screen_empty_announcement_text_tag))
    )

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (announcements.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.no_announcement),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.previewText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            }
        } else {
            items(sortedAnnouncements) { announcement ->
                AnnouncementItem(
                    modifier = Modifier.testTag(stringResource(R.string.news_screen_recent_announcements_tag)),
                    announcement = announcement,
                    onClick = {
                        if (announcement.state != AnnouncementState.PUBLISHED) {
                            onClickNotSentAnnouncement(announcement)
                        } else {
                            onClickAnnouncement(announcement)
                        }
                    }
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

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun NewsScreenPreview() {
    val isMember = true
    GedoiseTheme {
        PullToRefreshComponent(onRefresh = { }, isRefreshing = true) {
            Column {
                RecentAnnouncementSectionPreview()
            }

            if (isMember) {
                Box(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.medium)
                        .fillMaxSize()
                ) {
                    ExtendedFloatingActionButton(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { },
                        icon = {
                            Icon(
                                Icons.Filled.Edit,
                                stringResource(id = R.string.new_announcement)
                            )
                        },
                        text = { Text(text = stringResource(id = R.string.new_announcement)) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentAnnouncementSectionPreview() {
    GedoiseTheme {
        Column {
            RecentAnnouncementSection(
                announcements = announcementsFixture,
                onClickAnnouncement = { },
                onClickNotSentAnnouncement = { }
            )
        }
    }
}