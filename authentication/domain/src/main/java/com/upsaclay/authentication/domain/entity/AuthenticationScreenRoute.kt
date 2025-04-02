package com.upsaclay.authentication.domain.entity

import com.upsaclay.common.domain.entity.ScreenRoute

private const val AUTHENTICATION_ROUTE = "authentication_screen"
private const val FIRST_REGISTRATION_ROUTE = "first_registration_screen"
private const val SECOND_REGISTRATION_ROUTE = "second_registration_screen"
private const val THIRD_REGISTRATION_ROUTE = "third_registration_screen"

sealed class AuthenticationScreenRoute: ScreenRoute {
    data object Authentication : AuthenticationScreenRoute() {
        override val route: String = AUTHENTICATION_ROUTE
    }

    data object FirstRegistration : AuthenticationScreenRoute() {
        override val route: String = FIRST_REGISTRATION_ROUTE
    }

    data class SecondRegistration(
        val firstName: String,
        val lastName: String
    ) : AuthenticationScreenRoute() {
        override val route: String = "$SECOND_REGISTRATION_ROUTE?firstName=$firstName&lastName=$lastName"
        companion object {
            const val HARD_ROUTE = "$SECOND_REGISTRATION_ROUTE?firstName={firstName}&lastName={lastName}"
        }
    }

    data class ThirdRegistration(
        val firstName: String,
        val lastName: String,
        val schoolLevel: String
    ) : AuthenticationScreenRoute() {
        override val route: String = "$THIRD_REGISTRATION_ROUTE?firstName=$firstName&lastName=$lastName&schoolLevel=$schoolLevel"
        companion object {
            const val HARD_ROUTE = "$THIRD_REGISTRATION_ROUTE?firstName={firstName}&lastName={lastName}&schoolLevel={schoolLevel}"
        }
    }
}