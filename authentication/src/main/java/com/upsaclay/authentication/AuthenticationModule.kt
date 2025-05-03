package com.upsaclay.authentication

import com.upsaclay.authentication.presentation.authentication.AuthenticationViewModel
import com.upsaclay.authentication.presentation.registration.first.FirstRegistrationViewModel
import com.upsaclay.authentication.presentation.registration.second.SecondRegistrationViewModel
import com.upsaclay.authentication.presentation.registration.third.ThirdRegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authenticationModule = module {
    viewModelOf(::AuthenticationViewModel)
    viewModelOf(::FirstRegistrationViewModel)
    viewModelOf(::SecondRegistrationViewModel)
    viewModelOf(::ThirdRegistrationViewModel)
}