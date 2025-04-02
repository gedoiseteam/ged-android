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
import com.upsaclay.authentication.domain.entity.AuthenticationScreenRoute
import com.upsaclay.authentication.domain.entity.RegistrationErrorType
import com.upsaclay.authentication.domain.entity.RegistrationEvent
import com.upsaclay.authentication.presentation.screens.FirstRegistrationScreen
import com.upsaclay.authentication.presentation.screens.SecondRegistrationScreen
import com.upsaclay.authentication.presentation.viewmodels.FirstRegistrationViewModel
import com.upsaclay.authentication.presentation.viewmodels.SecondRegistrationViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FirstRegistrationScreenRouteUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val firstRegistrationViewModel: FirstRegistrationViewModel = mockk()
    private val secondRegistrationViewModel: SecondRegistrationViewModel = mockk()

    private val firstName = "firstName"
    private val lastName = "lastName"

    @Before
    fun setUp() {
        every { firstRegistrationViewModel.firstName } returns MutableStateFlow(firstName)
        every { firstRegistrationViewModel.lastName } returns MutableStateFlow(lastName)
        every { firstRegistrationViewModel.event } returns MutableSharedFlow()
        every { firstRegistrationViewModel.verifyNamesInputs() } returns true
        every { firstRegistrationViewModel.correctNamesInputs() } returns Unit
    }

    @Test
    fun navigate_to_second_registration_screen_when_click_on_next_button() {
        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AuthenticationScreenRoute.FirstRegistration.route) {
                composable(AuthenticationScreenRoute.FirstRegistration.route) {
                    FirstRegistrationScreen(
                        navController = navController,
                        firstRegistrationViewModel = firstRegistrationViewModel
                    )
                }

                composable(AuthenticationScreenRoute.SecondRegistration.HARD_ROUTE) {
                    SecondRegistrationScreen(
                        firstName = firstName,
                        lastName = lastName,
                        navController = navController,
                        secondRegistrationViewModel = secondRegistrationViewModel
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.registration_screen_next_button_tag)).performClick()

        // Then
        Assert.assertEquals(AuthenticationScreenRoute.SecondRegistration.HARD_ROUTE, navController.currentDestination?.route)
    }

    @Test
    fun empty_fields_show_error_message() {
        // Given
        every { firstRegistrationViewModel.event } returns MutableStateFlow(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR))

        // When
        rule.setContent {
            FirstRegistrationScreen(
                navController = navController,
                firstRegistrationViewModel = firstRegistrationViewModel
            )
        }

        // Then
        rule.onNodeWithText(rule.activity.getString(com.upsaclay.common.R.string.empty_fields_error)).assertExists()
    }
}