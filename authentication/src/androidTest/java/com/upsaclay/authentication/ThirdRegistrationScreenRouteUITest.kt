package com.upsaclay.authentication

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.authentication.domain.entity.RegistrationErrorType
import com.upsaclay.authentication.domain.entity.RegistrationEvent
import com.upsaclay.authentication.presentation.screens.ThirdRegistrationScreen
import com.upsaclay.authentication.presentation.viewmodels.ThirdRegistrationViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ThirdRegistrationScreenRouteUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val thirdRegistrationViewModel: ThirdRegistrationViewModel = mockk()

    private val firstName = "firstName"
    private val lastName = "lastName"
    private val schoolLevel = "schoolLevel"

    @Before
    fun setUp() {
        every { thirdRegistrationViewModel.email } returns "email"
        every { thirdRegistrationViewModel.password } returns "password"
        every { thirdRegistrationViewModel.register() } returns Unit
    }

    @Test
    fun components_are_disabled_when_loading() {
        // Given
        every { thirdRegistrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Loading)

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                firstName = firstName,
                lastName = lastName,
                schoolLevel = schoolLevel,
                navController = navController,
                thirdRegistrationViewModel = thirdRegistrationViewModel
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
        every { thirdRegistrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                firstName = firstName,
                lastName = lastName,
                schoolLevel = schoolLevel,
                navController = navController,
                thirdRegistrationViewModel = thirdRegistrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(com.upsaclay.common.R.string.empty_fields_error)).assertExists()
    }

    @Test
    fun invalid_email_format_show_error_message() {
        // Given
        every { thirdRegistrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.EMAIL_FORMAT_ERROR))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                firstName = firstName,
                lastName = lastName,
                schoolLevel = schoolLevel,
                navController = navController,
                thirdRegistrationViewModel = thirdRegistrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.error_incorrect_email_format)).assertExists()
    }

    @Test
    fun invalid_password_length_show_error_message() {
        // Given
        every { thirdRegistrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.PASSWORD_LENGTH_ERROR))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                firstName = firstName,
                lastName = lastName,
                schoolLevel = schoolLevel,
                navController = navController,
                thirdRegistrationViewModel = thirdRegistrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.error_password_length)).assertExists()
    }

    @Test
    fun email_already_exist_show_error_message() {
        // Given
        every { thirdRegistrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.USER_ALREADY_EXISTS))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                firstName = firstName,
                lastName = lastName,
                schoolLevel = schoolLevel,
                navController = navController,
                thirdRegistrationViewModel = thirdRegistrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.email_already_associated)).assertExists()
    }

    @Test
    fun unrecognized_paris_saclay_account_show_error_message() {
        // Given
        every { thirdRegistrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.UNRECOGNIZED_ACCOUNT))

        // When
        rule.setContent {
            ThirdRegistrationScreen(
                firstName = firstName,
                lastName = lastName,
                schoolLevel = schoolLevel,
                navController = navController,
                thirdRegistrationViewModel = thirdRegistrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(R.string.unrecognized_account)).assertExists()
    }
}