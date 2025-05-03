package com.upsaclay.authentication

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.upsaclay.authentication.presentation.authentication.AuthenticationDestination
import kotlinx.serialization.Serializable

@Serializable data object AuthenticationBaseRoute
@Serializable data object AuthenticationRoute

fun NavGraphBuilder.authenticationSection(
    onRegistrationClick: () -> Unit,
    onLoginClick: () -> Unit,
    registrationDestination: NavGraphBuilder.() -> Unit
) {
    navigation<AuthenticationBaseRoute>(startDestination = AuthenticationRoute) {
        composable<AuthenticationRoute> {
            AuthenticationDestination(
                onRegistrationClick = onRegistrationClick,
                onLoginClick = onLoginClick
            )
        }
    }
    registrationDestination()
}