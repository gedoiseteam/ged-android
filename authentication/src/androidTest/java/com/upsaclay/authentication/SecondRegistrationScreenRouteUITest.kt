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
import com.upsaclay.authentication.domain.entity.AuthenticationScreenRoute
import com.upsaclay.authentication.presentation.screens.SecondRegistrationScreen
import com.upsaclay.authentication.presentation.screens.ThirdRegistrationScreen
import com.upsaclay.authentication.presentation.viewmodels.SecondRegistrationViewModel
import com.upsaclay.authentication.presentation.viewmodels.ThirdRegistrationViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SecondRegistrationScreenRouteUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val secondRegistrationViewModel: SecondRegistrationViewModel = mockk()
    private val thirdRegistrationViewModel: ThirdRegistrationViewModel = mockk()

    private val firstName = "firstName"
    private val lastName = "lastName"
    private val schoolLevel = "schoolLevel"

    @Before
    fun setUp() {
        every { secondRegistrationViewModel.schoolLevels } returns listOf("GED 1", "GED 2", "GED 3", "GED 4")
        every { secondRegistrationViewModel.schoolLevel } returns MutableStateFlow(schoolLevel)
        every { secondRegistrationViewModel.updateSchoolLevel(any()) } returns Unit
    }

    @Test
    fun navigate_to_third_registration_screen_when_click_on_next_button() {
        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AuthenticationScreenRoute.SecondRegistration.HARD_ROUTE) {
                composable(AuthenticationScreenRoute.SecondRegistration.HARD_ROUTE) {
                    SecondRegistrationScreen(
                        firstName = firstName,
                        lastName = lastName,
                        navController = navController,
                        secondRegistrationViewModel = secondRegistrationViewModel
                    )
                }

                composable(AuthenticationScreenRoute.ThirdRegistration.HARD_ROUTE) {
                    ThirdRegistrationScreen(
                        firstName = firstName,
                        lastName = lastName,
                        schoolLevel = schoolLevel,
                        navController = navController,
                        thirdRegistrationViewModel = thirdRegistrationViewModel
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.registration_screen_next_button_tag)).performClick()

        // Then
        Assert.assertEquals(AuthenticationScreenRoute.ThirdRegistration.HARD_ROUTE, navController.currentDestination?.route)
    }
}