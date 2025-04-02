package com.upsaclay.authentication

import com.upsaclay.authentication.presentation.viewmodels.AuthenticationViewModel
import com.upsaclay.authentication.presentation.viewmodels.FirstRegistrationViewModel
import com.upsaclay.authentication.presentation.viewmodels.SecondRegistrationViewModel
import com.upsaclay.authentication.presentation.viewmodels.ThirdRegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authenticationModule = module {
    viewModelOf(::AuthenticationViewModel)
    viewModelOf(::FirstRegistrationViewModel)
    viewModelOf(::SecondRegistrationViewModel)
    viewModel { (firstName: String, lastName: String, schoolLevel: String) ->
        ThirdRegistrationViewModel(
            firstName = firstName,
            lastName = lastName,
            schoolLevel = schoolLevel,
            authenticationRepository = get(),
            userRepository = get()
        )
    }
}