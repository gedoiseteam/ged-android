package com.upsaclay.authentication

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.authentication.domain.entity.AuthenticationScreenState
import com.upsaclay.authentication.presentation.screens.AuthenticationScreen
import com.upsaclay.authentication.presentation.screens.FirstRegistrationScreen
import com.upsaclay.authentication.presentation.viewmodels.AuthenticationViewModel
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.Screen
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthenticationScreenUiTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val authenticationViewModel: AuthenticationViewModel = mockk()
    private val registrationViewModel: RegistrationViewModel = mockk()

    @Before
    fun setUp() {
        every { authenticationViewModel.email } returns "email"
        every { authenticationViewModel.password } returns "password"
        every { authenticationViewModel.screenState } returns MutableStateFlow(AuthenticationScreenState.DEFAULT)
        every { authenticationViewModel.login() } returns Unit
        every { authenticationViewModel.resetScreenState() } returns Unit
        every { authenticationViewModel.verifyInputs() } returns true
        every { authenticationViewModel.resetEmail() } returns Unit
        every { authenticationViewModel.resetPassword() } returns Unit
    }

    @Test
    fun navigate_to_registration_screen_when_click_registration_button() {
        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController, startDestination = Screen.AUTHENTICATION.route) {
                composable(Screen.AUTHENTICATION.route) {
                    AuthenticationScreen(
                        navController,
                        authenticationViewModel
                    )
                }

                composable(Screen.FIRST_REGISTRATION.route) {
                    FirstRegistrationScreen(
                        navController,
                        registrationViewModel
                    )
                }
            }
        }
        rule.onNodeWithTag(rule.activity.getString(R.string.authentication_screen_registration_button_tag)).performClick()

        // Then
        Assert.assertEquals(Screen.FIRST_REGISTRATION.route, navController.currentDestination?.route)
    }

    @Test
    fun data_should_be_cleared_when_navigate_to_registration() {
        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController, startDestination = Screen.AUTHENTICATION.route) {
                composable(Screen.AUTHENTICATION.route) {
                    AuthenticationScreen(
                        navController,
                        authenticationViewModel
                    )
                }

                composable(Screen.FIRST_REGISTRATION.route) {
                    FirstRegistrationScreen(
                        navController,
                        registrationViewModel
                    )
                }
            }
        }
        rule.onNodeWithTag(rule.activity.getString(R.string.authentication_screen_registration_button_tag)).performClick()

        // Then
        verify { authenticationViewModel.resetEmail() }
        verify { authenticationViewModel.resetPassword() }
        verify { authenticationViewModel.resetScreenState() }
    }

    @Test
    fun display_verify_email_dialog_when_email_is_not_verified() {
        // Given
        every { authenticationViewModel.screenState } returns MutableStateFlow(AuthenticationScreenState.EMAIL_NOT_VERIFIED)

        // When
        rule.setContent {
            AuthenticationScreen(
                navController,
                authenticationViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.authentication_screen_verify_email_dialog_tag)).assertExists()
    }

    @Test
    fun invalid_credentials_show_error_message() {
        // Given
        every { authenticationViewModel.screenState } returns MutableStateFlow(AuthenticationScreenState.AUTHENTICATION_ERROR)

        // When
        rule.setContent {
            AuthenticationScreen(
                navController,
                authenticationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.error_connection)).assertExists()
    }

    @Test
    fun empty_fields_show_error_message() {
        // Given
        every { authenticationViewModel.screenState } returns MutableStateFlow(AuthenticationScreenState.EMPTY_FIELDS_ERROR)

        // When
        rule.setContent {
            AuthenticationScreen(
                navController,
                authenticationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(com.upsaclay.common.R.string.empty_fields_error)).assertExists()
    }

    @Test
    fun user_not_found_show_error_message() {
        // Given
        every { authenticationViewModel.screenState } returns MutableStateFlow(AuthenticationScreenState.AUTHENTICATED_USER_NOT_FOUND)

        // When
        rule.setContent {
            AuthenticationScreen(
                navController,
                authenticationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.authenticated_user_not_found)).assertExists()
    }

    @Test
    fun send_to_many_request_show_error_message() {
        // Given
        every { authenticationViewModel.screenState } returns MutableStateFlow(AuthenticationScreenState.TOO_MANY_REQUESTS_ERROR)

        // When
        rule.setContent {
            AuthenticationScreen(
                navController,
                authenticationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.too_many_request_error)).assertExists()
    }
}