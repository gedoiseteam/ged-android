package com.upsaclay.gedoise

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.common.domain.userFixture
import com.upsaclay.gedoise.domain.entities.AccountScreenState
import com.upsaclay.gedoise.presentation.screens.AccountScreen
import com.upsaclay.gedoise.presentation.viewmodels.AccountViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val accountViewModel: AccountViewModel = mockk()

    @Before
    fun setUp() {
        every { accountViewModel.screenState } returns MutableStateFlow(AccountScreenState.READ)
        every { accountViewModel.currentUser } returns MutableStateFlow(userFixture)
        every { accountViewModel.profilePictureUri } returns Uri.EMPTY
        every { accountViewModel.updateProfilePictureUri(any()) } returns Unit
        every { accountViewModel.updateScreenState(any()) } returns Unit
        every { accountViewModel.resetProfilePictureUri() } returns Unit
        every { accountViewModel.deleteUserProfilePicture() } returns Unit
        every { accountViewModel.resetScreenState() } returns Unit
    }

    @Test
    fun delete_profile_picture_dialog_should_be_display_when_delete_profile_picture_button_is_clicked() {
        // When
        rule.setContent {
            AccountScreen(
                navController = navController,
                accountViewModel = accountViewModel
            )
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.account_screen_profile_picture_tag)).performClick()
        rule.onNodeWithTag(rule.activity.getString(R.string.account_screen_delete_profile_picture_button_tag)).performClick()

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.account_screen_delete_profile_picture_dialog_tag)).assertExists()
    }

    @Test
    fun error_snackbar_should_be_displayed_when_update_profile_picture_fails() {
        // Given
        every { accountViewModel.screenState } returns MutableStateFlow(AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR)

        // When
        rule.setContent {
            AccountScreen(
                navController = navController,
                accountViewModel = accountViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.account_screen_error_snackbar_tag)).assertExists()
    }

    @Test
    fun success_snackbar_should_be_displayed_when_update_profile_picture_succeeds() {
        // Given
        every { accountViewModel.screenState } returns MutableStateFlow(AccountScreenState.PROFILE_PICTURE_UPDATED)

        // When
        rule.setContent {
            AccountScreen(
                navController = navController,
                accountViewModel = accountViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.account_screen_success_snackbar_tag)).assertExists()
    }
}