package com.upsaclay.news

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.domain.userFixture
import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.domain.announcementsFixture
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.presentation.screens.CreateAnnouncementScreen
import com.upsaclay.news.presentation.screens.NewsScreen
import com.upsaclay.news.presentation.screens.ReadAnnouncementScreen
import com.upsaclay.news.presentation.viewmodels.CreateAnnouncementViewModel
import com.upsaclay.news.presentation.viewmodels.NewsViewModel
import com.upsaclay.news.presentation.viewmodels.ReadAnnouncementViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewsScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val newsViewModel: NewsViewModel = mockk()
    private val readAnnouncementViewModel: ReadAnnouncementViewModel = mockk()
    private val createAnnouncementViewModel: CreateAnnouncementViewModel = mockk()

    @Before
    fun setUp() {
        every { newsViewModel.announcementsPreview } returns flowOf(announcementsFixture)
        every { newsViewModel.currentUser } returns MutableStateFlow(userFixture)
        every { newsViewModel.isRefreshing } returns false
        every { readAnnouncementViewModel.announcement } returns MutableStateFlow(announcementFixture)
        every { readAnnouncementViewModel.screenState } returns MutableStateFlow(AnnouncementScreenState.DEFAULT)
        every { readAnnouncementViewModel.currentUser } returns MutableStateFlow(userFixture)
        every { readAnnouncementViewModel.deleteAnnouncement() } returns Unit
        every { createAnnouncementViewModel.screenState } returns MutableStateFlow(AnnouncementScreenState.DEFAULT)
        every { createAnnouncementViewModel.title } returns ""
        every { createAnnouncementViewModel.content } returns ""
        coEvery { newsViewModel.refreshAnnouncements() } returns Unit
    }

    @Test
    fun empty_announcements_show_empty_announcement_text() {
        // Given
        every { newsViewModel.announcementsPreview } returns flowOf(emptyList())

        // When
        rule.setContent {
            NewsScreen(
                navController = navController,
                newsViewModel = newsViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.news_screen_empty_announcement_text_tag))
            .assertExists()
    }

    @Test
    fun no_empty_announcements_show_announcements() {
        // When
        rule.setContent {
            NewsScreen(
                navController = navController,
                newsViewModel = newsViewModel
            )
        }

        // Then
        rule.onAllNodesWithTag(rule.activity.getString(R.string.news_screen_recent_announcements_tag))
            .apply {
                fetchSemanticsNodes().forEachIndexed { i, _ ->
                    get(i).assertExists()
                }
            }
    }

    @Test
    fun announcements_with_title_show_announcements_with_title() {
        // When
        rule.setContent {
            NewsScreen(
                navController = navController,
                newsViewModel = newsViewModel
            )
        }

        // Then
        rule.onAllNodesWithTag(rule.activity.getString(R.string.news_screen_recent_announcements_tag))
            .apply {
                fetchSemanticsNodes().forEachIndexed { i, _ ->
                    get(i).assert(hasText(announcementFixture.title!!))
                }
            }
    }

    @Test
    fun announcements_without_title_show_announcements_with_content() {
        // Given
        val content = announcementFixture.content.take(100)
        val announcement = announcementFixture.copy(title = null, content = content)
        every { newsViewModel.announcementsPreview } returns flowOf(listOf(announcement))

        // When
        rule.setContent {
            NewsScreen(
                navController = navController,
                newsViewModel = newsViewModel
            )
        }

        // Then
        rule.onAllNodesWithTag(rule.activity.getString(R.string.news_screen_recent_announcements_tag))
            .apply {
                fetchSemanticsNodes().forEachIndexed { i, _ ->
                    get(i).assert(hasText(content))
                }
            }
    }

    @Test
    fun clicking_announcement_navigate_to_read_announcement_screen() {
        // Given
        every { newsViewModel.announcementsPreview } returns flowOf(listOf(announcementFixture))
        val route = Screen.READ_ANNOUNCEMENT.route + "?announcementId={announcementId}"

        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Screen.NEWS.route) {
                composable(Screen.NEWS.route) {
                    NewsScreen(
                        navController = navController,
                        newsViewModel = newsViewModel
                    )
                }

                composable(route) {
                    ReadAnnouncementScreen(
                        announcementId = announcementFixture.id,
                        navController = navController,
                        readAnnouncementViewModel = readAnnouncementViewModel
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.news_screen_recent_announcements_tag))
            .performClick()

        // Then
        Assert.assertEquals(route, navController.currentDestination?.route)
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_announcement_title_tag)).assertExists()
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_announcement_content_tag)).assertExists()
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_announcement_content_tag)).assert(hasText(announcementFixture.content))
    }

    @Test
    fun create_announcement_button_should_be_displayed_when_user_is_member() {
        // Given
        every { newsViewModel.currentUser } returns MutableStateFlow(userFixture.copy(isMember = true))

        // When
        rule.setContent {
            NewsScreen(
                navController = navController,
                newsViewModel = newsViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.news_screen_create_announcement_button_tag)).assertExists()
    }

    @Test
    fun clicking_create_announcement_button_navigate_to_create_announcement_screen() {
        // Given
        every { newsViewModel.currentUser } returns MutableStateFlow(userFixture.copy(isMember = true))

        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Screen.NEWS.route) {
                composable(Screen.NEWS.route) {
                    NewsScreen(
                        navController = navController,
                        newsViewModel = newsViewModel
                    )
                }

                composable(Screen.CREATE_ANNOUNCEMENT.route) {
                    CreateAnnouncementScreen(
                        navController = navController,
                        createAnnouncementViewModel = createAnnouncementViewModel
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.news_screen_create_announcement_button_tag))
            .performClick()

        // Then
        Assert.assertEquals(Screen.CREATE_ANNOUNCEMENT.route, navController.currentDestination?.route)
    }

    @Test
    fun create_announcement_button_should_not_be_displayed_when_user_is_member() {
        // Given
        every { newsViewModel.currentUser } returns MutableStateFlow(userFixture)

        // When
        rule.setContent {
            NewsScreen(
                navController = navController,
                newsViewModel = newsViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.news_screen_create_announcement_button_tag)).assertDoesNotExist()
    }
}