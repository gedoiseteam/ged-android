package com.upsaclay.gedoise.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.common.presentation.components.ProfilePicture
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.gedoise.R
import com.upsaclay.gedoise.domain.entities.MainScreen
import com.upsaclay.gedoise.presentation.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    navController: NavController,
    profilePictureUrl: String?
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { },
                enabled = false
            ) {
                Image(
                    painter = painterResource(id = com.upsaclay.common.R.drawable.ged_logo),
                    contentDescription = stringResource(id = com.upsaclay.common.R.string.ged_logo_description),
                    contentScale = ContentScale.Fit
                )
            }
        },
        actions = {
            IconButton(
                onClick = { navController.navigate(MainScreen.Profile.route) },
                modifier = Modifier.clip(shape = CircleShape)
            ) {
                ProfilePicture(url = profilePictureUrl)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(title: String) {
    TopAppBar(
        title = {
            Text(text = title, fontWeight = FontWeight.Bold)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun MainBottomBar(
    navController: NavController,
    navigationItems: List<NavigationItem>
) {
    val currentRoute = remember {
        navController.currentDestination
    }

    NavigationBar {
        navigationItems.forEach { navigationItem ->
            val selected = navigationItem.screen.route == currentRoute?.route
            val iconRes = if (selected) navigationItem.filledIcon else navigationItem.outlinedIcon

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(navigationItem.screen.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (navigationItem.badges > 0) {
                                Badge { Text(text = navigationItem.badges.toString()) }
                            } else if (navigationItem.hasNews) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = stringResource(id = navigationItem.iconDescription)
                        )
                    }
                },
                label = { Text(text = stringResource(id = navigationItem.label)) }
            )
        }
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun MainTopBarPreview() {
    GedoiseTheme {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { },
                    enabled = false
                ) {
                    Image(
                        painter = painterResource(id = com.upsaclay.common.R.drawable.ged_logo),
                        contentDescription = stringResource(id = com.upsaclay.common.R.string.ged_logo_description),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { },
                    modifier = Modifier.clip(shape = CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = com.upsaclay.common.R.drawable.default_profile_picture),
                        contentDescription = stringResource(id = R.string.profile_icon_description),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MainBottomBarPreview() {
    val itemList = listOf(
        NavigationItem.Home(),
        NavigationItem.Message(badges = 5)
    )

    GedoiseTheme {
        MainBottomBar(rememberNavController(), itemList)
    }
}