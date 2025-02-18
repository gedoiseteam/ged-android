package com.upsaclay.news

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.domain.userFixture
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.presentation.screens.EditAnnouncementScreen
import com.upsaclay.news.presentation.screens.ReadAnnouncementScreen
import com.upsaclay.news.presentation.viewmodels.ReadAnnouncementViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReadAnnouncementScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val readAnnouncementViewModel: ReadAnnouncementViewModel = mockk()

    @Before
    fun setUp() {
        every { readAnnouncementViewModel.announcement } returns MutableStateFlow(announcementFixture)
        every { readAnnouncementViewModel.screenState } returns MutableStateFlow(AnnouncementScreenState.DEFAULT)
        every { readAnnouncementViewModel.currentUser } returns MutableStateFlow(userFixture)
        every { readAnnouncementViewModel.updateScreenState(any()) } returns Unit
    }

    @Test
    fun option_button_should_be_displayed_when_user_is_member_and_author() {
        // When
        rule.setContent {
            ReadAnnouncementScreen(
                announcementId = announcementFixture.id,
                navController = navController,
                readAnnouncementViewModel = readAnnouncementViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_option_button_tag)).assertExists()
    }

    @Test
    fun option_button_should_not_be_displayed_when_user_is_not_member() {
        // When
        rule.setContent {
            ReadAnnouncementScreen(
                announcementId = announcementFixture.id,
                navController = navController,
                readAnnouncementViewModel = readAnnouncementViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_sheet_edit_field_tag)).assertDoesNotExist()
    }

    @Test
    fun option_button_should_not_be_displayed_when_user_is_member_and_not_author() {
        // Given
        every { readAnnouncementViewModel.announcement } returns MutableStateFlow(announcementFixture.copy(author = userFixture2))

        // When
        rule.setContent {
            ReadAnnouncementScreen(
                announcementId = announcementFixture.id,
                navController = navController,
                readAnnouncementViewModel = readAnnouncementViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_sheet_edit_field_tag)).assertDoesNotExist()
    }

    @Test
    fun clicking_option_should_display_bottom_sheet() {
        // When
        rule.setContent {
            ReadAnnouncementScreen(
                announcementId = announcementFixture.id,
                navController = navController,
                readAnnouncementViewModel = readAnnouncementViewModel
            )
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_option_button_tag)).performClick()

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_bottom_sheet_tag)).assertExists()
    }

    @Test
    fun clicking_edit_sheet_field_should_navigate_to_edit_screen() {
        // Given
        val route = Screen.EDIT_ANNOUNCEMENT.route + "?announcementId={announcementId}"

        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Screen.NEWS.route) {
                composable(Screen.NEWS.route) {
                    ReadAnnouncementScreen(
                        announcementId = announcementFixture.id,
                        navController = navController,
                        readAnnouncementViewModel = readAnnouncementViewModel
                    )
                }

                composable(route) {
                    EditAnnouncementScreen(
                        announcementId = announcementFixture.id,
                        navController = navController,
                        editAnnouncementViewModel = mockk()
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_option_button_tag)).performClick()
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_sheet_edit_field_tag)).performClick()

        // Then
        Assert.assertEquals(route, navController.currentDestination?.route)
    }

    @Test
    fun clicking_delete_sheet_field_should_display_delete_dialog() {
        // When
        rule.setContent {
            ReadAnnouncementScreen(
                announcementId = announcementFixture.id,
                navController = navController,
                readAnnouncementViewModel = readAnnouncementViewModel
            )
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_option_button_tag)).performClick()
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_sheet_delete_field_tag)).performClick()

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_delete_dialog_tag)).assertExists()
    }

    @Test
    fun error_snackbar_should_displayed_when_delete_announcement_fails() {
        // Given
        every { readAnnouncementViewModel.screenState } returns MutableStateFlow(AnnouncementScreenState.ERROR)

        // When
        rule.setContent {
            ReadAnnouncementScreen(
                announcementId = announcementFixture.id,
                navController = navController,
                readAnnouncementViewModel = readAnnouncementViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.read_screen_error_snackbar_tag)).assertExists()
    }
}