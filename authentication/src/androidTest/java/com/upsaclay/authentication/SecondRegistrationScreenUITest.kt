package com.upsaclay.authentication

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.authentication.domain.entity.AuthenticationScreen
import com.upsaclay.authentication.presentation.screens.SecondRegistrationScreen
import com.upsaclay.authentication.presentation.screens.ThirdRegistrationScreen
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SecondRegistrationScreenUITest {
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
        every { registrationViewModel.updateSchoolLevel(any()) } returns Unit
    }

    @Test
    fun navigate_to_third_registration_screen_when_click_on_next_button() {
        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AuthenticationScreen.SecondRegistration.route) {
                composable(AuthenticationScreen.SecondRegistration.route) {
                    SecondRegistrationScreen(
                        navController = navController,
                        registrationViewModel = registrationViewModel
                    )
                }

                composable(AuthenticationScreen.ThirdRegistration.route) {
                    ThirdRegistrationScreen(
                        navController = navController,
                        registrationViewModel = registrationViewModel
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.registration_screen_next_button_tag)).performClick()

        // Then
        Assert.assertEquals(AuthenticationScreen.ThirdRegistration.route, navController.currentDestination?.route)
    }
}