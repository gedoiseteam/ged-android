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
import com.upsaclay.authentication.domain.entity.AuthenticationScreen
import com.upsaclay.authentication.domain.entity.RegistrationErrorType
import com.upsaclay.authentication.domain.entity.RegistrationEvent
import com.upsaclay.authentication.presentation.screens.FirstRegistrationScreen
import com.upsaclay.authentication.presentation.screens.SecondRegistrationScreen
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FirstRegistrationScreenUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val registrationViewModel: RegistrationViewModel = mockk()

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
    }

    @Test
    fun navigate_to_second_registration_screen_when_click_on_next_button() {
        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AuthenticationScreen.FirstRegistration.route) {
                composable(AuthenticationScreen.FirstRegistration.route) {
                    FirstRegistrationScreen(
                        navController = navController,
                        registrationViewModel = registrationViewModel
                    )
                }

                composable(AuthenticationScreen.SecondRegistration.route) {
                    SecondRegistrationScreen(
                        navController = navController,
                        registrationViewModel = registrationViewModel
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.registration_screen_next_button_tag)).performClick()

        // Then
        Assert.assertEquals(AuthenticationScreen.SecondRegistration.route, navController.currentDestination?.route)
    }

    @Test
    fun empty_fields_show_error_message() {
        // Given
        every { registrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR))

        // When
        rule.setContent {
            FirstRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(com.upsaclay.common.R.string.empty_fields_error)).assertExists()
    }
}