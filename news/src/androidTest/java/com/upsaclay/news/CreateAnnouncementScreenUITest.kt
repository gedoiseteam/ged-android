package com.upsaclay.news

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.presentation.screens.CreateAnnouncementScreen
import com.upsaclay.news.presentation.viewmodels.CreateAnnouncementViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateAnnouncementScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val createAnnouncementViewModel: CreateAnnouncementViewModel = mockk()

    @Before
    fun setUp() {
        every { createAnnouncementViewModel.title } returns ""
        every { createAnnouncementViewModel.content } returns ""
        every { createAnnouncementViewModel.createAnnouncement() } returns Unit
        every { createAnnouncementViewModel.updateTitle(any()) } returns Unit
        every { createAnnouncementViewModel.updateContent(any()) } returns Unit
    }

    @Test
    fun post_button_should_be_disabled_only_when_content_is_empty() {
        // Given
        every { createAnnouncementViewModel.title } returns "title"

        // When
        rule.setContent {
            CreateAnnouncementScreen(
                navController,
                createAnnouncementViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(com.upsaclay.common.R.string.publish))
            .assert(isNotEnabled())

    }

    @Test
    fun post_button_should_be_disabled_only_when_content_is_not_empty() {
        // Given
        every { createAnnouncementViewModel.content } returns "some content"

        // When
        rule.setContent {
            CreateAnnouncementScreen(
                navController,
                createAnnouncementViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(com.upsaclay.common.R.string.publish))
            .assert(isEnabled())
    }
}