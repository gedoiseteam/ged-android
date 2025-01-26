package com.upsaclay.gedoise.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.presentation.screens.AuthenticationScreen
import com.upsaclay.authentication.presentation.screens.EmailVerificationScreen
import com.upsaclay.authentication.presentation.screens.FirstRegistrationScreen
import com.upsaclay.authentication.presentation.screens.SecondRegistrationScreen
import com.upsaclay.authentication.presentation.screens.ThirdRegistrationScreen
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.utils.showToast
import com.upsaclay.gedoise.R
import com.upsaclay.gedoise.data.BottomNavigationItem
import com.upsaclay.gedoise.presentation.components.HomeTopBar
import com.upsaclay.gedoise.presentation.components.MainBottomBar
import com.upsaclay.gedoise.presentation.components.SplashScreen
import com.upsaclay.message.domain.usecase.ConvertConversationJsonUseCase
import com.upsaclay.message.presentation.screens.ChatScreen
import com.upsaclay.message.presentation.screens.ConversationScreen
import com.upsaclay.message.presentation.screens.CreateConversationScreen
import com.upsaclay.news.presentation.screens.CreateAnnouncementScreen
import com.upsaclay.news.presentation.screens.EditAnnouncementScreen
import com.upsaclay.news.presentation.screens.NewsScreen
import com.upsaclay.news.presentation.screens.ReadAnnouncementScreen
import com.upsaclay.news.presentation.viewmodels.EditAnnouncementViewModel
import com.upsaclay.profile.presentation.screens.AccountScreen
import com.upsaclay.profile.presentation.screens.ProfileScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(mainViewModel: MainViewModel = koinViewModel()) {
    val navController = rememberNavController()
    val authenticationState by mainViewModel.authenticationState.collectAsState()
    val currentUser by mainViewModel.currentUser.collectAsState()
    val registrationViewModel: RegistrationViewModel = koinViewModel()

    val startDestination = when (authenticationState) {
        AuthenticationState.IDLE -> Screen.SPLASH.route
        AuthenticationState.AUTHENTICATED -> Screen.NEWS.route
        else -> Screen.AUTHENTICATION.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.SPLASH.route) {
            SplashScreen()
        }

        composable(Screen.AUTHENTICATION.route) {
            AuthenticationScreen(navController = navController)
        }

        composable(Screen.FIRST_REGISTRATION.route) {
            FirstRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        composable(Screen.SECOND_REGISTRATION.route) {
            SecondRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        composable(Screen.THIRD_REGISTRATION.route) {
            ThirdRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        composable(Screen.EMAIL_VERIFICATION.route + "email={email}") { backStackEntry ->
            backStackEntry.arguments?.getString("email")?.let { email ->
                EmailVerificationScreen(email = email)
            }
        }

        composable(Screen.NEWS.route) {
            MainNavigationBars(
                navController = navController,
                topBar = { HomeTopBar(navController = navController, currentUser?.profilePictureUrl) },
                bottomNavigationItems = mainViewModel.bottomNavigationItem.values.toList(),
            ) {
                NewsScreen(navController = navController)
            }
        }

        composable(Screen.READ_ANNOUNCEMENT.route + "?announcementId={announcementId}") { backStackEntry ->
            val announcementId = backStackEntry.arguments?.getString("announcementId")

            announcementId?.let {
                ReadAnnouncementScreen(
                    announcementId = it,
                    navController = navController
                )
            } ?: navController.popBackStack()
        }

        composable(Screen.CREATE_ANNOUNCEMENT.route) {
            CreateAnnouncementScreen(navController = navController)
        }

        composable(
            route = Screen.EDIT_ANNOUNCEMENT.route + "?announcementId={announcementId}",
            arguments = listOf(navArgument("announcementId") { type = StringType })
        ) { backStackEntry ->
            val announcementId = backStackEntry.arguments?.getString("announcementId")

            announcementId?.let {
                val editAnnouncementViewModel: EditAnnouncementViewModel = koinViewModel(
                    parameters = { parametersOf(announcementId) }
                )
                EditAnnouncementScreen(
                    navController = navController,
                    editAnnouncementViewModel = editAnnouncementViewModel
                )
            } ?: navController.popBackStack()
        }

        composable(Screen.CONVERSATIONS.route) {
            MainNavigationBars(
                navController = navController,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.messages),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    )
                },
                bottomNavigationItems = mainViewModel.bottomNavigationItem.values.toList(),
            ) {
                ConversationScreen(navController = navController,)
            }
        }

        composable(Screen.CREATE_CONVERSATION.route) {
            CreateConversationScreen(navController = navController)
        }

        composable(route = Screen.CHAT.route + "conversation={conversation}") { backStackEntry ->
            backStackEntry.arguments?.getString("conversation")?.let {
                ChatScreen(conversation = ConvertConversationJsonUseCase.from(it), navController = navController)
            } ?: run {
                navController.navigate(Screen.CONVERSATIONS.route)
                showToast(context = LocalContext.current, stringRes = com.upsaclay.common.R.string.occurred_error)
            }
        }

        composable(Screen.PROFILE.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.ACCOUNT.route) {
            AccountScreen(navController = navController)
        }
    }
}

@Composable
private fun MainNavigationBars(
    navController: NavController,
    topBar: @Composable () -> Unit,
    bottomNavigationItems: List<BottomNavigationItem>,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        topBar = topBar,
        bottomBar = {
            MainBottomBar(
                navController = navController,
                bottomNavigationItems = bottomNavigationItems
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                )
        ) {
            content()
        }
    }
}