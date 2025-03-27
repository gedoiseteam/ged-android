package com.upsaclay.gedoise.domain.entities

import com.upsaclay.common.domain.entity.Screen

private const val PROFILE_ROUTE = "profile_screen"
private const val ACCOUNT_ROUTE = "account_screen"
private const val SPLASH_ROUTE = "splash_screen"

sealed class MainScreen: Screen {
    data object Profile: MainScreen() {
        override val route: String = PROFILE_ROUTE
    }

    data object Account: MainScreen() {
        override val route: String = ACCOUNT_ROUTE
    }

    data object Splash: MainScreen() {
        override val route: String = SPLASH_ROUTE
    }
}