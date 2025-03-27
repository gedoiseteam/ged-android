package com.upsaclay.authentication

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.authentication.domain.entity.AuthenticationScreen
import com.upsaclay.authentication.domain.entity.RegistrationErrorType
import com.upsaclay.authentication.domain.entity.RegistrationEvent
import com.upsaclay.authentication.presentation.screens.EmailVerificationScreen
import com.upsaclay.authentication.presentation.screens.ThirdRegistrationScreen
import com.upsaclay.authentication.presentation.viewmodels.EmailVerificationViewModel
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.userFixture
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ThirdRegistrationScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val registrationViewModel: RegistrationViewModel = mockk()
    private val emailVerificationViewModel: EmailVerificationViewModel = mockk()

    @Before
    fun setUp() {
        every { registrationViewModel.firstName } returns "firstName"
        every { registrationViewModel.lastName } returns "lastName"
        every { registrationViewModel.email } returns "email"
        every { registrationViewModel.password } returns "password"
        every { registrationViewModel.schoolLevels } returns listOf("GED 1", "GED 2", "GED 3", "GED 4")
        every { registrationViewModel.schoolLevel } returns "GED 1"
        every { registrationViewModel.resetFirstName() } returns Unit
        every { registrationViewModel.resetLastName() } returns Unit
        every { registrationViewModel.resetEmail() } returns Unit
        every { registrationViewModel.resetPassword() } returns Unit
        every { registrationViewModel.resetSchoolLevel() } returns Unit
        every { registrationViewModel.verifyNamesInputs() } returns true
        every { registrationViewModel.register() } returns Unit
        every { emailVerificationViewModel.sendVerificationEmail() } returns Unit
    }

    @Test
    fun navigate_to_email_verification_screen_when_user_is_registered() {
        // Given
        every { registrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Registered)

        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AuthenticationScreen.ThirdRegistration.route) {
                composable(AuthenticationScreen.ThirdRegistration.route) {
                    ThirdRegistrationScreen(
                        navController = navController,
                        registrationViewModel = registrationViewModel
                    )
                }

                composable(AuthenticationScreen.EmailVerification.HARD_ROUTE){
                    EmailVerificationScreen(
                        email = userFixture.email,
                        navController = navController,
                        emailVerificationViewModel = emailVerificationViewModel
                    )
                }
            }
        }

        // Then
        Assert.assertEquals(AuthenticationScreen.EmailVerification.HARD_ROUTE, navController.currentDestination?.route)
    }

    @Test
    fun components_are_disabled_when_loading() {
        // Given
        every { registrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Loading)

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.registration_screen_next_button_tag)).assert(isNotEnabled())
        rule.onNodeWithTag(rule.activity.getString(R.string.registration_screen_email_input_tag)).assert(isNotEnabled())
        rule.onNodeWithTag(rule.activity.getString(R.string.registration_screen_password_input_tag)).assert(isNotEnabled())
    }

    @Test
    fun empty_fields_show_error_message() {
        // Given
        every { registrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(com.upsaclay.common.R.string.empty_fields_error)).assertExists()
    }

    @Test
    fun invalid_email_format_show_error_message() {
        // Given
        every { registrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.EMAIL_FORMAT_ERROR))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.error_incorrect_email_format)).assertExists()
    }

    @Test
    fun invalid_password_length_show_error_message() {
        // Given
        every { registrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.PASSWORD_LENGTH_ERROR))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.error_password_length)).assertExists()
    }

    @Test
    fun email_already_exist_show_error_message() {
        // Given
        every { registrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.USER_ALREADY_EXISTS))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.email_already_associated)).assertExists()
    }

    @Test
    fun unrecognized_paris_saclay_account_show_error_message() {
        // Given
        every { registrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.UNRECOGNIZED_ACCOUNT))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.unrecognized_account)).assertExists()
    }
}