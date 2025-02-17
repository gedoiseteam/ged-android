package com.upsaclay.authentication

import com.upsaclay.authentication.presentation.viewmodels.AuthenticationViewModel
import com.upsaclay.authentication.presentation.viewmodels.EmailVerificationViewModel
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authenticationModule = module {
    viewModelOf(::AuthenticationViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModel { (email: String) ->
        EmailVerificationViewModel(
            email = email,
            sendVerificationEmailUseCase = get(),
            isEmailVerifiedUseCase = get(),
            setUserAuthenticatedUseCase = get()
        )
    }
}