package com.upsaclay.authentication

import com.upsaclay.authentication.domain.usecase.GenerateHashUseCase
import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.authentication.domain.usecase.LoginUseCase
import com.upsaclay.authentication.domain.usecase.RegisterUseCase
import com.upsaclay.authentication.domain.usecase.SendVerificationEmailUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.authentication.domain.usecase.VerifyEmailFormatUseCase
import com.upsaclay.authentication.presentation.viewmodels.AuthenticationViewModel
import com.upsaclay.authentication.presentation.viewmodels.EmailVerificationViewModel
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
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

    singleOf(::CreateUserUseCase)
    singleOf(::GenerateHashUseCase)
    singleOf(::IsUserAuthenticatedUseCase)
    singleOf(::IsEmailVerifiedUseCase)
    singleOf(::LoginUseCase)
    singleOf(::RegisterUseCase)
    singleOf(::SendVerificationEmailUseCase)
    singleOf(::SetUserAuthenticatedUseCase)
    singleOf(::VerifyEmailFormatUseCase)
}