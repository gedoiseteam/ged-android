package com.upsaclay.news

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.presentation.screens.EditAnnouncementScreen
import com.upsaclay.news.presentation.viewmodels.EditAnnouncementViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditAnnouncementScreenRouteUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val editAnnouncementViewModel: EditAnnouncementViewModel = mockk()

    @Before
    fun setUp() {
        every { editAnnouncementViewModel.announcement } returns MutableStateFlow(announcementFixture)
        every { editAnnouncementViewModel.isAnnouncementModified } returns MutableStateFlow(false)
        every { editAnnouncementViewModel.title } returns MutableStateFlow("")
        every { editAnnouncementViewModel.content } returns MutableStateFlow("")
        every { editAnnouncementViewModel.event } returns MutableSharedFlow()
    }

    @Test
    fun save_button_should_be_disabled_when_no_changes() {
        // Given
        every { editAnnouncementViewModel.isAnnouncementModified } returns MutableStateFlow(false)

        // When
        rule.setContent {
            EditAnnouncementScreen(
                announcementId = announcementFixture.id,
                navController = navController,
                editAnnouncementViewModel = editAnnouncementViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(com.upsaclay.common.R.string.save))
            .assert(isNotEnabled())
    }

    @Test
    fun save_button_should_be_disabled_when_content_is_empty() {
        // Given
        every { editAnnouncementViewModel.isAnnouncementModified } returns MutableStateFlow(true)
        every { editAnnouncementViewModel.content } returns MutableStateFlow("")

        // When
        rule.setContent {
            EditAnnouncementScreen(
                announcementId = announcementFixture.id,
                navController = navController,
                editAnnouncementViewModel = editAnnouncementViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(com.upsaclay.common.R.string.save))
            .assert(isNotEnabled())
    }
}