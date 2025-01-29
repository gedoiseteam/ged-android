package com.upsaclay.gedoise.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.upsaclay.common.presentation.components.LoadingDialog
import com.upsaclay.common.presentation.components.ProfilePicture
import com.upsaclay.common.presentation.components.ProfilePictureWithIcon
import com.upsaclay.common.presentation.components.SensibleActionDialog
import com.upsaclay.common.presentation.components.SmallTopBarBack
import com.upsaclay.common.presentation.components.SmallTopBarEdit
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.showToast
import com.upsaclay.common.utils.userFixture
import com.upsaclay.gedoise.R
import com.upsaclay.gedoise.domain.entities.AccountInfo
import com.upsaclay.gedoise.domain.entities.AccountScreenState
import com.upsaclay.gedoise.presentation.components.AccountInfoItem
import com.upsaclay.gedoise.presentation.components.AccountModelBottomSheet
import com.upsaclay.gedoise.presentation.viewmodels.AccountViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavController,
    accountViewModel: AccountViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val accountScreenState by accountViewModel.accountScreenState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteProfilePictureDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    val user by accountViewModel.currentUser.collectAsState()

    val hideBottomSheet: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
            }
        }
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                accountViewModel.updateProfilePictureUri(it)
                accountViewModel.updateAccountScreenState(AccountScreenState.EDIT)
            }
        }
    )

    when (accountScreenState) {
        AccountScreenState.PROFILE_PICTURE_UPDATED -> {
            showLoadingDialog = false
            showToast(context, R.string.profile_picture_updated)
            accountViewModel.updateAccountScreenState(AccountScreenState.READ)
        }

        AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR -> {
            showLoadingDialog = false
            showToast(context, R.string.error_updating_profile_picture)
            accountViewModel.updateAccountScreenState(AccountScreenState.READ)
        }

        AccountScreenState.LOADING -> showLoadingDialog = true

        else -> {}
    }

    if (showDeleteProfilePictureDialog) {
        SensibleActionDialog(
            text = stringResource(id = R.string.delete_profile_picture_dialog_text),
            confirmText = stringResource(id = com.upsaclay.common.R.string.delete),
            onConfirm = {
                showDeleteProfilePictureDialog = false
                accountViewModel.deleteUserProfilePicture()
            },
            onCancel = { showDeleteProfilePictureDialog = false },
            onDismiss = { showDeleteProfilePictureDialog = false }
        )
    }

    if (showLoadingDialog) {
        LoadingDialog()
    }

    val accountInfos: ImmutableList<AccountInfo> = persistentListOf(
        AccountInfo(
            stringResource(id = com.upsaclay.common.R.string.last_name),
            user?.lastName ?: ""
        ),
        AccountInfo(
            stringResource(id = com.upsaclay.common.R.string.first_name),
            user?.firstName ?: ""
        ),
        AccountInfo(
            stringResource(id = com.upsaclay.common.R.string.email),
            user?.email ?: ""
        ),
        AccountInfo(
            stringResource(id = com.upsaclay.common.R.string.school_level),
            user?.schoolLevel ?: ""
        )
    )

    Scaffold(
        topBar = {
            com.upsaclay.gedoise.presentation.components.AccountTopBar(
                isEdited = accountScreenState == AccountScreenState.EDIT,
                onSaveClick = { accountViewModel.updateUserProfilePicture() },
                onCancelClick = {
                    accountViewModel.resetProfilePictureUri()
                    accountViewModel.updateAccountScreenState(AccountScreenState.READ)
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = it.calculateTopPadding(),
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.medium
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfilePictureSection(
                    isEdited = accountScreenState == AccountScreenState.EDIT,
                    profilePictureUri = accountViewModel.profilePictureUri,
                    profilePictureUrl = user?.profilePictureUrl,
                    onClick = {
                        if (accountScreenState == AccountScreenState.EDIT) {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else {
                            showBottomSheet = true
                        }
                    }
                )

                accountInfos.forEach { accountInfo ->
                    AccountInfoItem(
                        modifier = Modifier.fillMaxWidth(),
                        accountInfo = accountInfo
                    )
                }

                if (showBottomSheet) {
                    AccountModelBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        onNewProfilePictureClick = {
                            hideBottomSheet()
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        showDeleteProfilePicture = user?.profilePictureUrl != null,
                        onDeleteProfilePictureClick = {
                            hideBottomSheet()
                            showDeleteProfilePictureDialog = true
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun ProfilePictureSection(
    isEdited: Boolean,
    profilePictureUri: Uri?,
    profilePictureUrl: String?,
    onClick: () -> Unit
) {
    val scaleImage = 1.8f

    profilePictureUri?.let { uri ->
        if (isEdited) {
            ProfilePictureWithIcon(
                uri = uri,
                iconVector = Icons.Default.Edit,
                scale = scaleImage,
                onClick = onClick
            )
        } else {
            ProfilePicture(
                uri = uri,
                scale = scaleImage,
                onClick = onClick
            )
        }
    } ?: run {
        ProfilePictureWithIcon(
            url = profilePictureUrl,
            iconVector = Icons.Default.Edit,
            scale = scaleImage,
            onClick = onClick
        )
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview
@Composable
private fun AccountScreenPreview() {
    val scaleImage = 1.8f
    val sizeImage = 100.dp * scaleImage
    val hasPictureChanged = false
    val isEdited = false

    val accountInfos: ImmutableList<AccountInfo> = persistentListOf(
        AccountInfo(
            stringResource(id = com.upsaclay.common.R.string.last_name),
            userFixture.lastName
        ),
        AccountInfo(
            stringResource(id = com.upsaclay.common.R.string.first_name),
            userFixture.firstName
        ),
        AccountInfo(
            stringResource(id = com.upsaclay.common.R.string.email),
            userFixture.email
        ),
        AccountInfo(
            stringResource(id = com.upsaclay.common.R.string.school_level),
            userFixture.schoolLevel
        )
    )

    GedoiseTheme {
        Scaffold(
            topBar = {
                if (isEdited) {
                    SmallTopBarEdit(
                        title = stringResource(id = R.string.account_informations),
                        onCancelClick = { },
                        onSaveClick = { }
                    )
                } else {
                    SmallTopBarBack(
                        title = stringResource(id = R.string.account_informations),
                        onBackClick = { }
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = it.calculateTopPadding(),
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        bottom = MaterialTheme.spacing.medium
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(sizeImage)) {
                    Image(
                        painter = painterResource(id = com.upsaclay.common.R.drawable.default_profile_picture),
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                    if (!hasPictureChanged) {
                        ProfilePictureWithIcon(
                            url = "",
                            scale = scaleImage,
                            iconVector = Icons.Default.Edit,
                            onClick = {}
                        )
                    }
                }

                accountInfos.forEach { accountInfo ->
                    com.upsaclay.gedoise.presentation.components.AccountInfoItem(
                        modifier = Modifier.fillMaxWidth(),
                        accountInfo = accountInfo
                    )
                }
            }
        }
    }
}