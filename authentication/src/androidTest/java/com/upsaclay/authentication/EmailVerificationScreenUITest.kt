package com.upsaclay.authentication

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.authentication.presentation.screens.EmailVerificationScreen
import com.upsaclay.authentication.presentation.viewmodels.EmailVerificationViewModel
import com.upsaclay.common.domain.userFixture
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EmailVerificationScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val emailVerificationViewModel: EmailVerificationViewModel = mockk()

    @Before
    fun setUp() {
        every { emailVerificationViewModel.screenState } returns MutableStateFlow(AuthenticationScreenState.DEFAULT)
        every { emailVerificationViewModel.sendVerificationEmail() } returns Unit
    }

    @Test
    fun components_are_disabled_when_loading() {
        // Given
        every { emailVerificationViewModel.screenState } returns MutableStateFlow(AuthenticationScreenState.LOADING)

        // When
        rule.setContent {
            EmailVerificationScreen(
                email = userFixture.email,
                navController = navController,
                emailVerificationViewModel = emailVerificationViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.email_verification_screen_finish_button_tag)).assert(isNotEnabled())
        rule.onNodeWithTag(rule.activity.getString(R.string.email_verification_screen_forward_email_button_tag)).assert(isNotEnabled())
    }

    @Test
    fun forward_email_button_should_be_disabled_after_click() {
        // When
        rule.setContent {
            EmailVerificationScreen(
                email = userFixture.email,
                navController = navController,
                emailVerificationViewModel = emailVerificationViewModel
            )
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.email_verification_screen_forward_email_button_tag)).performClick()

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.email_verification_screen_forward_email_button_tag)).assert(isNotEnabled())
    }
}