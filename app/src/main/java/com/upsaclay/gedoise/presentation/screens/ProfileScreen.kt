package com.upsaclay.gedoise.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.domain.userFixture
import com.upsaclay.common.presentation.components.ClickableItem
import com.upsaclay.common.presentation.components.LoadingDialog
import com.upsaclay.common.presentation.components.ProfilePicture
import com.upsaclay.common.presentation.components.SensibleActionDialog
import com.upsaclay.common.presentation.components.SmallTopBarBack
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.gedoise.R
import com.upsaclay.gedoise.presentation.viewmodels.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val user by profileViewModel.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val dividerColor = if (isSystemInDarkTheme()) GedoiseColor.DarkGray else Color.LightGray

    DisposableEffect(Unit) {
        onDispose { showLogoutDialog = false }
    }

    if (showLogoutDialog) {
        SensibleActionDialog(
            modifier = Modifier.testTag(stringResource(id = R.string.profile_screen_logout_dialog_tag)),
            title = stringResource(id = R.string.logout),
            text = stringResource(id = R.string.logout_dialog_message),
            cancelText = stringResource(id = com.upsaclay.common.R.string.cancel),
            confirmText = stringResource(id = R.string.logout),
            onConfirm = {
                profileViewModel.logout()
                showLogoutDialog = false
            },
            onCancel = { showLogoutDialog = false }
        )
    }

    Scaffold(
        topBar = {
            SmallTopBarBack(
                onBackClick = { navController.popBackStack() },
                title = stringResource(id = R.string.profile)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
            Column {
                TopSection(
                    profilePictureUrl = user?.profilePictureUrl,
                    userFullName = user?.fullName ?: "Unknown"
                )

                HorizontalDivider(color = dividerColor)

                ClickableItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(stringResource(id = R.string.profile_screen_account_info_button_tag)),
                    text = { Text(text = stringResource(id = R.string.account_informations)) },
                    icon = {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(id = com.upsaclay.common.R.drawable.ic_person),
                            contentDescription = stringResource(id = R.string.account_icon_description)
                        )
                    },
                    onClick = { navController.navigate(Screen.ACCOUNT.route) }
                )

                ClickableItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(stringResource(id = R.string.profile_screen_logout_button_tag)),
                    text = {
                        Text(
                            text = stringResource(id = R.string.logout),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = com.upsaclay.common.R.drawable.ic_logout),
                            contentDescription = stringResource(id = R.string.logout_icon_description),
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    }
}

@Composable
private fun TopSection(profilePictureUrl: String?, userFullName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.spacing.medium,
                end = MaterialTheme.spacing.medium,
                bottom = MaterialTheme.spacing.medium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePicture(
            url = profilePictureUrl,
            scale = 0.7f
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

        Text(
            text = userFullName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    val dividerColor = if (isSystemInDarkTheme()) GedoiseColor.DarkGray else Color.LightGray

    GedoiseTheme {

        if (showLogoutDialog) {
            SensibleActionDialog(
                title = stringResource(id = R.string.logout),
                text = stringResource(id = R.string.logout_dialog_message),
                cancelText = stringResource(id = com.upsaclay.common.R.string.cancel),
                confirmText = stringResource(id = R.string.logout),
                onConfirm = { showLogoutDialog = false },
                onCancel = { showLogoutDialog = false }
            )
        }

        if (showLoadingDialog) {
            LoadingDialog(message = stringResource(R.string.disconnection))
        }

        Scaffold(
            topBar = {
                SmallTopBarBack(
                    onBackClick = { },
                    title = stringResource(id = R.string.profile)
                )
            }
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = contentPadding.calculateTopPadding())
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = MaterialTheme.spacing.medium,
                                end = MaterialTheme.spacing.medium,
                                bottom = MaterialTheme.spacing.medium
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = com.upsaclay.common.R.drawable.default_profile_picture),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(70.dp)
                        )

                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

                        Text(
                            text = userFixture.firstName + " " + userFixture.lastName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(color = dividerColor)

                    ClickableItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = { Text(text = stringResource(id = R.string.account_informations)) },
                        icon = {
                            Icon(
                                modifier = Modifier.size(28.dp),
                                painter = painterResource(id = com.upsaclay.common.R.drawable.ic_person),
                                contentDescription = stringResource(id = R.string.account_icon_description)
                            )
                        },
                        onClick = { }
                    )

                    ClickableItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Text(
                                text = stringResource(id = R.string.logout),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = com.upsaclay.common.R.drawable.ic_logout),
                                contentDescription = stringResource(id = R.string.logout_icon_description),
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = { showLogoutDialog = true }
                    )
                }
            }
        }
    }
}