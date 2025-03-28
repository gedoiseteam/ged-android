package com.upsaclay.authentication.domain.entity

import com.upsaclay.common.domain.entity.ScreenRoute

private const val AUTHENTICATION_ROUTE = "authentication_screen"
private const val FIRST_REGISTRATION_ROUTE = "first_registration_screen"
private const val SECOND_REGISTRATION_ROUTE = "second_registration_screen"
private const val THIRD_REGISTRATION_ROUTE = "third_registration_screen"
private const val EMAIL_VERIFICATION_ROUTE = "email_verification_screen"

sealed class AuthenticationScreenRoute: ScreenRoute {
    data object Authentication : AuthenticationScreenRoute() {
        override val route: String = AUTHENTICATION_ROUTE
    }

    data object FirstRegistration : AuthenticationScreenRoute() {
        override val route: String = FIRST_REGISTRATION_ROUTE
    }

    data object SecondRegistration : AuthenticationScreenRoute() {
        override val route: String = SECOND_REGISTRATION_ROUTE
    }

    data object ThirdRegistration : AuthenticationScreenRoute() {
        override val route: String = THIRD_REGISTRATION_ROUTE
    }

    data class EmailVerification(val email: String) : AuthenticationScreenRoute() {
        override val route: String = "$EMAIL_VERIFICATION_ROUTE?email=$email"
        companion object {
            const val HARD_ROUTE = "$EMAIL_VERIFICATION_ROUTE?email={email}"
        }
    }
}