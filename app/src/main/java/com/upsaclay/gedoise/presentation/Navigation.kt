package com.upsaclay.gedoise.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.domain.entity.AuthenticationScreenRoute
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.presentation.screens.AuthenticationScreen
import com.upsaclay.authentication.presentation.screens.EmailVerificationScreen
import com.upsaclay.authentication.presentation.screens.FirstRegistrationScreen
import com.upsaclay.authentication.presentation.screens.SecondRegistrationScreen
import com.upsaclay.authentication.presentation.screens.ThirdRegistrationScreen
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.ScreenRoute
import com.upsaclay.common.utils.showToast
import com.upsaclay.gedoise.R
import com.upsaclay.gedoise.domain.entities.MainScreenRoute
import com.upsaclay.gedoise.presentation.components.HomeTopBar
import com.upsaclay.gedoise.presentation.components.MainBottomBar
import com.upsaclay.gedoise.presentation.components.MainTopBar
import com.upsaclay.gedoise.presentation.components.SplashScreen
import com.upsaclay.gedoise.presentation.screens.AccountScreen
import com.upsaclay.gedoise.presentation.screens.ProfileScreen
import com.upsaclay.gedoise.presentation.viewmodels.NavigationViewModel
import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.MessageScreenRoute
import com.upsaclay.message.presentation.screens.ChatScreen
import com.upsaclay.message.presentation.screens.ConversationScreen
import com.upsaclay.message.presentation.screens.CreateConversationScreen
import com.upsaclay.news.domain.entity.NewsScreenRoute
import com.upsaclay.news.presentation.screens.CreateAnnouncementScreen
import com.upsaclay.news.presentation.screens.EditAnnouncementScreen
import com.upsaclay.news.presentation.screens.NewsScreen
import com.upsaclay.news.presentation.screens.ReadAnnouncementScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import org.koin.androidx.compose.koinViewModel

@Composable
fun Navigation(
    navigationViewModel: NavigationViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val authenticationState by navigationViewModel.authenticationState.collectAsState()
    val currentUser by navigationViewModel.currentUser.collectAsState()
    val registrationViewModel: RegistrationViewModel = koinViewModel()
    val navigationItems by combine(
        navigationViewModel.homeNavigationItem,
        navigationViewModel.messageNavigationItem
    ) { home, message ->
        listOf(home, message)
    }.collectAsState(emptyList())

    val startDestination = when (authenticationState) {
        AuthenticationState.WAITING -> MainScreenRoute.Splash.route
        AuthenticationState.AUTHENTICATED -> NewsScreenRoute.News.route
        else -> AuthenticationScreenRoute.Authentication.route
    }
    navController.addOnDestinationChangedListener { controller, destination, _ ->
        navigationViewModel.setCurrentScreen(getScreen(controller, destination))
    }

    LaunchedEffect(Unit) {
        navigationViewModel.routeToNavigate.collectLatest {
            navController.navigate(it) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainScreenRoute.Splash.route) {
            SplashScreen()
        }

        composable(AuthenticationScreenRoute.Authentication.route) {
            registrationViewModel.resetAllValues()
            AuthenticationScreen(navController = navController)
        }

        composable(AuthenticationScreenRoute.FirstRegistration.route) {
            FirstRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        composable(AuthenticationScreenRoute.SecondRegistration.route) {
            SecondRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        composable(AuthenticationScreenRoute.ThirdRegistration.route) {
            ThirdRegistrationScreen(
                navController = navController,
                registrationViewModel = registrationViewModel
            )
        }

        composable(AuthenticationScreenRoute.EmailVerification.HARD_ROUTE) { backStackEntry ->
            backStackEntry.arguments?.getString("email")?.let { email ->
                EmailVerificationScreen(email = email, navController = navController)
            }
        }

        composable(NewsScreenRoute.News.route) {
            MainNavigationBars(
                navController = navController,
                topBar = {
                    HomeTopBar(
                        navController = navController,
                        currentUser?.profilePictureUrl
                    )
                },
                navigationItems = navigationItems,
            ) {
                NewsScreen(navController = navController)
            }
        }

        composable(NewsScreenRoute.ReadAnnouncement.HARD_ROUTE) { backStackEntry ->
            val announcementId = backStackEntry.arguments?.getString("announcementId")

            announcementId?.let {
                ReadAnnouncementScreen(
                    announcementId = it,
                    navController = navController
                )
            } ?: navController.popBackStack()
        }

        composable(NewsScreenRoute.CreateAnnouncement.route) {
            CreateAnnouncementScreen(navController = navController)
        }

        composable(NewsScreenRoute.EditAnnouncement.HARD_ROUTE) { backStackEntry ->
            val announcementId = backStackEntry.arguments?.getString("announcementId")

            announcementId?.let {
                EditAnnouncementScreen(
                    announcementId = announcementId,
                    navController = navController
                )
            } ?: navController.popBackStack()
        }

        composable(MessageScreenRoute.Conversation.route) {
            val snackbarHostState = remember { SnackbarHostState() }

            MainNavigationBars(
                navController = navController,
                topBar = { MainTopBar(title = stringResource(R.string.messages)) },
                snackbarHostState = snackbarHostState,
                navigationItems = navigationItems,
            ) {
                ConversationScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }

        composable(MessageScreenRoute.CreateConversation.route) {
            CreateConversationScreen(navController = navController)
        }

        composable(MessageScreenRoute.Chat.HARD_ROUTE) { backStackEntry ->
            backStackEntry.arguments?.getString("conversation")?.let {
                ChatScreen(
                    conversation = ConversationMapper.fromJson(it),
                    navController = navController
                )
            } ?: run {
                navController.navigate(MessageScreenRoute.Conversation.route)
                showToast(
                    context = LocalContext.current,
                    stringRes = com.upsaclay.common.R.string.occurred_error
                )
            }
        }

        composable(MainScreenRoute.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(MainScreenRoute.Account.route) {
            AccountScreen(navController = navController)
        }
    }
}

@Composable
private fun MainNavigationBars(
    navController: NavController,
    topBar: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigationItems: List<NavigationItem>,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        topBar = topBar,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { Snackbar(it) }
        },
        bottomBar = {
            MainBottomBar(
                navController = navController,
                navigationItems = navigationItems
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
        ) {
            content()
        }
    }
}

private fun getScreen(controller: NavController, destination: NavDestination): ScreenRoute? {
    return when(destination.route) {
        MessageScreenRoute.Chat.HARD_ROUTE -> {
            controller.currentBackStackEntry?.arguments?.getString("conversation")?.let { announcementId ->
                val conversation = ConversationMapper.fromJson(announcementId)
                MessageScreenRoute.Chat(conversation)
            }
        }

        else -> null
    }
}