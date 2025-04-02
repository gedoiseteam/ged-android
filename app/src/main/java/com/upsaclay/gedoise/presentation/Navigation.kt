package com.upsaclay.gedoise.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.domain.entity.AuthenticationScreenRoute
import com.upsaclay.authentication.presentation.screens.AuthenticationScreen
import com.upsaclay.authentication.presentation.screens.FirstRegistrationScreen
import com.upsaclay.authentication.presentation.screens.SecondRegistrationScreen
import com.upsaclay.authentication.presentation.screens.ThirdRegistrationScreen
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
import com.upsaclay.message.presentation.components.CreateConversationFAB
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
    val currentUser by navigationViewModel.currentUser.collectAsState()
    val navigationItems by combine(
        navigationViewModel.homeNavigationItem,
        navigationViewModel.messageNavigationItem
    ) { home, message ->
        listOf(home, message)
    }.collectAsState(emptyList())

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
        startDestination = MainScreenRoute.Splash.route
    ) {
        composable(MainScreenRoute.Splash.route) {
            SplashScreen()
        }

        composable(NewsScreenRoute.News.route) {
            MainScaffold(
                navController = navController,
                topBar = {
                    HomeTopBar(
                        navController = navController,
                        currentUser?.profilePictureUrl
                    )
                },
                navigationItems = navigationItems,
                floatingActionButton = {
                    if (currentUser?.isMember == true) {
                        ExtendedFloatingActionButton(
                            modifier = Modifier
                                .testTag(stringResource(id = com.upsaclay.news.R.string.news_screen_create_announcement_button_tag)),
                            text = { Text(text = stringResource(id = com.upsaclay.news.R.string.new_announcement)) },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            icon = {
                                Icon(
                                    Icons.Filled.Edit,
                                    stringResource(id = com.upsaclay.news.R.string.new_announcement)
                                )
                            },
                            onClick = { navController.navigate(NewsScreenRoute.CreateAnnouncement.route) }
                        )
                    }
                }
            ) {
                NewsScreen(navController = navController)
            }
        }

        composable(MessageScreenRoute.Conversation.route) {
            val snackbarHostState = remember { SnackbarHostState() }

            MainScaffold(
                navController = navController,
                topBar = { MainTopBar(title = stringResource(R.string.messages)) },
                snackbarHostState = snackbarHostState,
                navigationItems = navigationItems,
                floatingActionButton = {
                    CreateConversationFAB(
                        modifier = Modifier
                            .testTag(stringResource(id = com.upsaclay.message.R.string.conversation_screen_create_conversation_button_tag)),
                        onClick = { navController.navigate(MessageScreenRoute.CreateConversation.route) },
                    )
                }
            ) {
                ConversationScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }

        composable(AuthenticationScreenRoute.Authentication.route) {
            AuthenticationScreen(navController = navController)
        }

        composable(AuthenticationScreenRoute.FirstRegistration.route) {
            FirstRegistrationScreen(
                navController = navController
            )
        }

        composable(AuthenticationScreenRoute.SecondRegistration.HARD_ROUTE) { backStackEntry ->
            val firstName = backStackEntry.arguments?.getString("firstName")
            val lastName = backStackEntry.arguments?.getString("lastName")

            if (firstName == null || lastName == null) {
                navController.popBackStack()
                return@composable
            }

            SecondRegistrationScreen(
                firstName = firstName,
                lastName = lastName,
                navController = navController
            )
        }

        composable(AuthenticationScreenRoute.ThirdRegistration.HARD_ROUTE) { backStackEntry ->
            val firstName = backStackEntry.arguments?.getString("firstName")
            val lastName = backStackEntry.arguments?.getString("lastName")
            val schoolLevel = backStackEntry.arguments?.getString("schoolLevel")

            if (firstName == null || lastName == null || schoolLevel == null) {
                navController.popBackStack()
                return@composable
            }

            ThirdRegistrationScreen(
                firstName = firstName,
                lastName = lastName,
                schoolLevel = schoolLevel,
                navController = navController
            )
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
private fun MainScaffold(
    navController: NavController,
    topBar: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigationItems: List<NavigationItem>,
    floatingActionButton: @Composable (() -> Unit)? = null,
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
        },
        floatingActionButton = floatingActionButton ?: {}
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