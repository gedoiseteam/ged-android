package com.upsaclay.authentication.domain.entity

import com.upsaclay.common.domain.entity.Screen

private const val AUTHENTICATION_ROUTE = "authentication_screen"
private const val FIRST_REGISTRATION_ROUTE = "first_registration_screen"
private const val SECOND_REGISTRATION_ROUTE = "second_registration_screen"
private const val THIRD_REGISTRATION_ROUTE = "third_registration_screen"
private const val EMAIL_VERIFICATION_ROUTE = "email_verification_screen"

sealed class AuthenticationScreen: Screen {
    data object Authentication : AuthenticationScreen() {
        override val route: String = AUTHENTICATION_ROUTE
    }

    data object FirstRegistration : AuthenticationScreen() {
        override val route: String = FIRST_REGISTRATION_ROUTE
    }

    data object SecondRegistration : AuthenticationScreen() {
        override val route: String = SECOND_REGISTRATION_ROUTE
    }

    data object ThirdRegistration : AuthenticationScreen() {
        override val route: String = THIRD_REGISTRATION_ROUTE
    }

    data class EmailVerification(val email: String) : AuthenticationScreen() {
        override val route: String = "$EMAIL_VERIFICATION_ROUTE?email=$email"
        companion object {
            const val HARD_ROUTE = "$EMAIL_VERIFICATION_ROUTE?email={email}"
        }
    }
}