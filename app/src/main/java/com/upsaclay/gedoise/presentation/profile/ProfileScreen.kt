package com.upsaclay.gedoise.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.common.domain.model.Screen
import com.upsaclay.common.presentation.ClickableMenuItemData
import com.upsaclay.common.presentation.components.CircularProgressBar
import com.upsaclay.common.presentation.components.ProfilePicture
import com.upsaclay.common.presentation.components.SimpleClickableItem
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.userFixture
import com.upsaclay.gedoise.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel = koinViewModel()) {
    val user = profileViewModel.user.collectAsState(initial = null).value
    val clickableMenuItemsData: ImmutableList<ClickableMenuItemData> =
        buildProfileMenuItemData(navController, profileViewModel)

    Scaffold(
        topBar = { ProfileTopBar(navController = navController) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
            user?.let { user ->
                Column {
                    TopSection(
                        profilePictureUrl = user.profilePictureUrl,
                        userFullName = user.fullName,
                        userEmail = user.email
                    )

                    HorizontalDivider()

                    clickableMenuItemsData.forEach { menuItem ->
                        SimpleClickableItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = menuItem.text,
                            icon = menuItem.icon,
                            onClick = menuItem.onClick
                        )
                    }
                }
            } ?: CircularProgressBar(
                modifier = Modifier.align(Alignment.Center),
                scale = 3f
            )
        }
    }
}

@Composable
private fun TopSection(profilePictureUrl: String?, userFullName: String, userEmail: String) {
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
            ProfilePicture(
                imageUrl = profilePictureUrl,
                scaleImage = 0.7f
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

            Text(
                text = userFullName,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.profile),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = com.upsaclay.common.R.string.arrow_back_icon_description)
                )
            }
        }
    )
}

private fun buildProfileMenuItemData(
    navController: NavController,
    profileViewModel: ProfileViewModel
): ImmutableList<ClickableMenuItemData> = persistentListOf(
    ClickableMenuItemData(
        text = { Text(text = stringResource(id = R.string.account_informations)) },
        icon = {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = com.upsaclay.common.R.drawable.ic_person),
                contentDescription = stringResource(id = R.string.account_icon_description)
            )
        },
        onClick = { navController.navigate(Screen.ACCOUNT.route) }
    ),
    ClickableMenuItemData(
        text = {
            Text(text = stringResource(id = R.string.support))
        },
        icon = {
            Icon(
                painter = painterResource(id = com.upsaclay.common.R.drawable.ic_support),
                contentDescription = stringResource(id = R.string.support_icon_description)
            )
        },
        onClick = {navController.navigate((Screen.SUPPORT.route))}
    ),
    ClickableMenuItemData(
        text = {
            Text(
                text = stringResource(id = R.string.logout),
                color = GedoiseColor.Red
            )
        },
        icon = {
            Icon(
                painter = painterResource(id = com.upsaclay.common.R.drawable.ic_logout),
                contentDescription = stringResource(id = R.string.logout_icon_description),
                tint = GedoiseColor.Red
            )
        },
        onClick = { profileViewModel.logout() }
    )

)

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview
@Composable
fun ProfileScreenPreview() {
    val isLoading = false

    GedoiseTheme {
        Scaffold(
            topBar = { ProfileTopBar(navController = rememberNavController()) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                if (isLoading) {
                    CircularProgressBar(
                        modifier = Modifier.align(Alignment.Center),
                        scale = 3f
                    )
                } else {
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
                                    .border(1.dp, Color.LightGray, CircleShape)
                            )

                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

                            Text(
                                text = userFixture.firstName + " " + userFixture.lastName,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        HorizontalDivider()

                        profileMenuItemsDataFixture.forEach { menuItem ->
                            SimpleClickableItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = menuItem.text,
                                icon = menuItem.icon,
                                onClick = menuItem.onClick
                            )
                        }
                    }
                }
            }
        }
    }
}

private val profileMenuItemsDataFixture: ImmutableList<ClickableMenuItemData> = persistentListOf(
    ClickableMenuItemData(
        text = { Text(text = stringResource(id = R.string.account_informations)) },
        icon = {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = com.upsaclay.common.R.drawable.ic_person),
                contentDescription = stringResource(id = R.string.account_icon_description)
            )
        },
        onClick = {}
    ),
//    ClickableMenuItemData(
//        text = {
//            Text(text = stringResource(id = R.string.settings))
//        },
//        icon = {
//            Icon(
//                modifier = Modifier.size(28.dp),
//                painter = painterResource(id = com.upsaclay.common.R.drawable.ic_settings),
//                contentDescription = stringResource(id = R.string.settings_icon_description)
//            )
//        }
//    ),
   ClickableMenuItemData(
       text = {
            Text(text = stringResource(id = R.string.support))
       },
       icon = {
           Icon(
               painter = painterResource(id = com.upsaclay.common.R.drawable.ic_support),
               contentDescription = stringResource(id = R.string.support_icon_description)
           )
        },
       onClick = { }
    ),
    ClickableMenuItemData(
        text = {
            Text(
                text = stringResource(id = R.string.logout),
                color = GedoiseColor.Red
            )
               },
        icon = {
            Icon(
                painter = painterResource(id = com.upsaclay.common.R.drawable.ic_logout),
                contentDescription = stringResource(id = R.string.logout_icon_description),
                tint = GedoiseColor.Red
            )
               },
        onClick = {}
               )
)
