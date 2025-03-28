package com.upsaclay.gedoise.domain.entities

import com.upsaclay.common.domain.entity.ScreenRoute

private const val PROFILE_ROUTE = "profile_screen"
private const val ACCOUNT_ROUTE = "account_screen"
private const val SPLASH_ROUTE = "splash_screen"

sealed class MainScreenRoute: ScreenRoute {
    data object Profile: MainScreenRoute() {
        override val route: String = PROFILE_ROUTE
    }

    data object Account: MainScreenRoute() {
        override val route: String = ACCOUNT_ROUTE
    }

    data object Splash: MainScreenRoute() {
        override val route: String = SPLASH_ROUTE
    }
}