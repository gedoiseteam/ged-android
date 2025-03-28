package com.upsaclay.gedoise

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.upsaclay.common.domain.userFixture
import com.upsaclay.gedoise.domain.entities.MainScreenRoute
import com.upsaclay.gedoise.presentation.screens.AccountScreen
import com.upsaclay.gedoise.presentation.screens.ProfileScreen
import com.upsaclay.gedoise.presentation.viewmodels.ProfileViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileScreenRouteUITest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var navController: TestNavHostController = mockk()
    private val profileViewModel: ProfileViewModel = mockk()

    @Before
    fun setUp() {
        every { profileViewModel.currentUser } returns MutableStateFlow(userFixture)
        every { profileViewModel.logout() } returns Unit
    }

    @Test
    fun logout_dialog_should_be_shown_when_logout_button_is_clicked() {
        // When
        rule.setContent {
            ProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.profile_screen_logout_button_tag)).performClick()


        // Then
        rule.onNodeWithTag(rule.activity.getString(R.string.profile_screen_logout_button_tag)).assertExists()
    }

    @Test
    fun navigate_to_account_screen_when_account_info_button_is_clicked() {
        // When
        rule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            NavHost(navController = navController, startDestination = MainScreenRoute.Profile.route) {
                composable(MainScreenRoute.Profile.route) {
                    ProfileScreen(
                        navController = navController,
                        profileViewModel = profileViewModel
                    )
                }

                composable(MainScreenRoute.Account.route) {
                    AccountScreen(
                        navController = navController
                    )
                }
            }
        }

        rule.onNodeWithTag(rule.activity.getString(R.string.profile_screen_account_info_button_tag)).performClick()

        // Then
        Assert.assertEquals(MainScreenRoute.Account.route, navController.currentDestination?.route)
    }
}