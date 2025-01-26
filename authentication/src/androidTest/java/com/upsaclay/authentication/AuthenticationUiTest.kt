package com.upsaclay.authentication

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.presentation.screens.AuthenticationScreen
import com.upsaclay.authentication.presentation.viewmodels.AuthenticationViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.upsaclay.common.R as CoreResource

class AuthenticationUiTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var buttonLogin: SemanticsMatcher
    private lateinit var errorConnectionText: SemanticsMatcher
    private lateinit var errorInputText: SemanticsMatcher
    private lateinit var circularProgressBar: SemanticsMatcher
    private lateinit var inputFields: SemanticsMatcher
    private lateinit var navController: NavController
    private lateinit var authenticationViewModel: AuthenticationViewModel

    @Before
    fun setUp() {
        navController = mockk()
        authenticationViewModel = mockk()

        every {
            authenticationViewModel.email
        } returns ""
        every {
            authenticationViewModel.password
        } returns ""
        every {
            authenticationViewModel.login()
        } returns Unit
        every {
            authenticationViewModel.updateEmail(any())
        } returns Unit
        every {
            authenticationViewModel.updateEmail(any())
        } returns Unit

        buttonLogin = hasText(rule.activity.getString(R.string.login)) and hasClickAction()
        errorConnectionText = hasText(rule.activity.getString(R.string.error_connection))
        errorInputText = hasText(rule.activity.getString(CoreResource.string.empty_fields_error))
        circularProgressBar = hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
    }

    @Test
    fun show_error_input_text_when_authentication_state_is_error_input() {
        val authenticationState = MutableStateFlow(AuthenticationState.UNAUTHENTICATED)
        every {
            authenticationViewModel.authenticationState
        } returns authenticationState

        rule.setContent {
            AuthenticationScreen(
                navController = navController,
                authenticationViewModel = authenticationViewModel
            )
        }

        rule.onNode(buttonLogin).performClick()
        authenticationState.value = AuthenticationState.INPUTS_EMPTY_ERROR
        rule.onNode(errorInputText).assertExists("Error empty text don't exist")
    }

    @Test
    fun show_error_authentication_text_when_authentication_state_is_error_authentication() {
        val authenticationState = MutableStateFlow(AuthenticationState.UNAUTHENTICATED)
        every {
            authenticationViewModel.authenticationState
        } returns authenticationState

        rule.setContent {
            AuthenticationScreen(
                navController = navController,
                authenticationViewModel = authenticationViewModel
            )
        }

        rule.onNode(buttonLogin).performClick()
        authenticationState.value = AuthenticationState.AUTHENTICATION_ERROR
        rule.onNode(errorConnectionText).assertExists("Error authentication text don't exist")
    }

    @Test
    fun show_circular_progress_bar_when_authentication_state_is_loading() {
        val authenticationState = MutableStateFlow(AuthenticationState.UNAUTHENTICATED)
        every {
            authenticationViewModel.authenticationState
        } returns authenticationState

        rule.setContent {
            AuthenticationScreen(
                navController = navController,
                authenticationViewModel = authenticationViewModel
            )
        }

        rule.onNode(buttonLogin).performClick()
        authenticationState.value = AuthenticationState.LOADING
        rule.onNode(circularProgressBar).assertExists("Error circular bar don't exist")
    }

    @Test
    fun show_circular_progress_bar_disappear_when_authentication_state_goes_loading_to_another_state() {
        val authenticationState = MutableStateFlow(AuthenticationState.UNAUTHENTICATED)
        every {
            authenticationViewModel.authenticationState
        } returns authenticationState

        rule.setContent {
            AuthenticationScreen(
                navController = navController,
                authenticationViewModel = authenticationViewModel
            )
        }

        rule.onNode(buttonLogin).performClick()
        authenticationState.value = AuthenticationState.LOADING
        rule.onNode(circularProgressBar).assertExists("Error circular bar don't exist")
        authenticationState.value = AuthenticationState.AUTHENTICATED
        rule.onNode(circularProgressBar).assertDoesNotExist()
    }
}