package com.upsaclay.news.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.presentation.components.PullToRefreshComponent
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.news.R
import com.upsaclay.news.announcementsFixture
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.presentation.components.AnnouncementItem
import com.upsaclay.news.presentation.viewmodels.NewsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewsScreen(
    newsViewModel: NewsViewModel = koinViewModel(),
    navController: NavController
) {
    val announcements = newsViewModel.announcements.collectAsState(emptyList()).value
    val user by newsViewModel.currentUser.collectAsState()
    val isRefreshing = newsViewModel.isRefreshing

    LaunchedEffect(Unit) {
        newsViewModel.resetAnnouncementState()
        newsViewModel.refreshAnnouncements()
    }

    PullToRefreshComponent(
        onRefresh = { newsViewModel.refreshAnnouncements() },
        isRefreshing = isRefreshing
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            RecentAnnouncementSection(
                announcements = announcements,
                onClickAnnouncement = {
                    navController.navigate(Screen.READ_ANNOUNCEMENT.route + "announcementId=${it.id}")
                }
            )
            PostSection()
        }

        if (user?.isMember == true) {
            Box(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.medium)
                    .fillMaxSize()
            ) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    text = { Text(text = stringResource(id = R.string.new_announcement)) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    icon = {
                        Icon(
                            Icons.Filled.Edit,
                            stringResource(id = R.string.new_announcement)
                        )
                    },
                    onClick = { navController.navigate(Screen.CREATE_ANNOUNCEMENT.route) }
                )
            }
        }
    }
}

@Composable
private fun RecentAnnouncementSection(
    announcements: List<Announcement>,
    onClickAnnouncement: (Announcement) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val sortedAnnouncements = announcements.sortedByDescending { it.date }

    Column(modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)) {
        Text(
            text = stringResource(id = R.string.recent_announcements),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        LazyColumn(
            modifier = Modifier.heightIn(max = screenHeight * 0.46f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (announcements.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.no_announcements),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(sortedAnnouncements) { announcement ->
                    AnnouncementItem(
                        announcement = announcement,
                        onClick = { onClickAnnouncement(announcement) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PostSection() {
    Column {
        Text(
            text = stringResource(id = R.string.news_ged),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
        )

        // TODO : Implémenter la récupération des posts
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
                PostSection()
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
                onClickAnnouncement = {}
            )
        }
    }
}