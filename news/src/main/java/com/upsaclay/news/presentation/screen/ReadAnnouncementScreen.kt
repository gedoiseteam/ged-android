package com.upsaclay.news.presentation.screen

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
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.upsaclay.common.domain.model.Screen
import com.upsaclay.common.presentation.ClickableMenuItemData
import com.upsaclay.common.presentation.components.LoadingDialog
import com.upsaclay.common.presentation.components.SensibleActionDialog
import com.upsaclay.common.presentation.components.SimpleClickableItem
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.showToast
import com.upsaclay.news.R
import com.upsaclay.news.announcementFixture
import com.upsaclay.news.presentation.components.AnnouncementItem
import com.upsaclay.news.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadAnnouncementScreen(modifier: Modifier = Modifier, navController: NavController, newsViewModel: NewsViewModel = koinViewModel()) {
    val displayAnnouncement = newsViewModel.displayedAnnouncement

    if (displayAnnouncement == null) {
        newsViewModel.updateAnnouncementState(com.upsaclay.news.domain.model.AnnouncementState.ANNOUNCEMENT_DISPLAY_ERROR)
        navController.popBackStack()
    }

    LaunchedEffect(Unit) {
        newsViewModel.resetAnnouncementState()
        newsViewModel.refreshDisplayAnnouncement(displayAnnouncement!!.id)
    }

    val context = LocalContext.current
    val announcement = newsViewModel.displayedAnnouncement!!
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteAnnouncementDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val user = newsViewModel.user.collectAsState(initial = null).value
    val state =
        newsViewModel.announcementState.collectAsState(initial = com.upsaclay.news.domain.model.AnnouncementState.DEFAULT).value
    val hideBottomSheet: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
            }
        }
    }

    LaunchedEffect(state) {
        when (state) {
            com.upsaclay.news.domain.model.AnnouncementState.ANNOUNCEMENT_DELETE_ERROR -> {
                showLoadingDialog = false
                showToast(context, R.string.announcement_delete_error)
            }

            com.upsaclay.news.domain.model.AnnouncementState.ANNOUNCEMENT_DELETED -> {
                showLoadingDialog = false
                navController.popBackStack()
            }

            com.upsaclay.news.domain.model.AnnouncementState.LOADING -> showLoadingDialog = true

            else -> {}
        }
    }

    if (showLoadingDialog) {
        LoadingDialog(message = stringResource(id = com.upsaclay.common.R.string.deletion))
    }

    val bottomSheetItemData: List<ClickableMenuItemData> = listOf(
        ClickableMenuItemData(
            text = { Text(text = stringResource(id = R.string.edit_announcement)) },
            icon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) },
            onClick = {
                val jsonAnnouncement = newsViewModel.convertAnnouncementToJson(announcement)
                navController.navigate(
                    Screen.EDIT_ANNOUNCEMENT.route + "?editedAnnouncement=$jsonAnnouncement"
                )
                hideBottomSheet()
            }
        ),
        ClickableMenuItemData(
            text = {
                Text(
                    text = stringResource(id = R.string.delete_announcement),
                    color = GedoiseColor.Red
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = GedoiseColor.Red
                )
            },
            onClick = {
                hideBottomSheet()
                showDeleteAnnouncementDialog = true
            }
        )
    )

    if (showDeleteAnnouncementDialog) {
        DeleteAnnouncementDialog(
            onCancel = { showDeleteAnnouncementDialog = false },
            onConfirm = {
                showDeleteAnnouncementDialog = false
                newsViewModel.deleteAnnouncement(announcement)
            }
        )
    }

    user?.let {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.medium
                )
                .verticalScroll(rememberScrollState())
        ) {
            if (user.isMember && announcement.author == user) {
                EditableTopSection(
                    announcement = announcement,
                    onEditClick = { showBottomSheet = true }
                )
            } else {
                AnnouncementItem(announcement = announcement)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            announcement.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            }

            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyLarge
            )

            if (showBottomSheet) {
                EditAnnouncementModelBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    menuItemData = bottomSheetItemData
                )
            }
        }
    }
}

@Composable
private fun EditableTopSection(announcement: com.upsaclay.news.domain.model.Announcement, onEditClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AnnouncementItem(
            announcement = announcement,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.smallMedium))

        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditAnnouncementModelBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    menuItemData: List<ClickableMenuItemData>
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        menuItemData.forEach { menuItemData ->
            SimpleClickableItem(
                modifier = Modifier.fillMaxWidth(),
                text = menuItemData.text,
                icon = menuItemData.icon,
                onClick = menuItemData.onClick
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))
    }
}

@Composable
private fun DeleteAnnouncementDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    SensibleActionDialog(
        message = stringResource(id = R.string.delete_announcement_dialog_text),
        onDismiss = onCancel,
        confirmText = stringResource(id = com.upsaclay.common.R.string.delete),
        onConfirm = onConfirm,
        onCancel = onCancel
    )
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
            AnnouncementItem(announcement = announcement)

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            announcement.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

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
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyLarge

            )
        }
    }
}

@Preview
@Composable
private fun DeleteAnnouncementDialogPreview() {
    GedoiseTheme {
        DeleteAnnouncementDialog(
            onCancel = {},
            onConfirm = {}
        )
    }
}