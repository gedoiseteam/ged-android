package com.upsaclay.authentication

import com.upsaclay.authentication.domain.usecase.GenerateHashUseCase
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.authentication.domain.usecase.IsUserEmailVerifiedUseCase
import com.upsaclay.common.domain.usecase.CreateNewUserUseCase
import com.upsaclay.authentication.domain.usecase.LoginUseCase
import com.upsaclay.authentication.domain.usecase.LogoutUseCase
import com.upsaclay.authentication.domain.usecase.RegisterUseCase
import com.upsaclay.authentication.domain.usecase.SendVerificationEmailUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.authentication.domain.usecase.VerifyEmailFormatUseCase
import com.upsaclay.authentication.presentation.AuthenticationViewModel
import com.upsaclay.authentication.presentation.registration.RegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authenticationModule = module {
    viewModelOf(::AuthenticationViewModel)
    viewModelOf(::RegistrationViewModel)

    singleOf(::CreateNewUserUseCase)
    singleOf(::GenerateHashUseCase)
    singleOf(::IsUserAuthenticatedUseCase)
    singleOf(::IsUserEmailVerifiedUseCase)
    singleOf(::LoginUseCase)
    singleOf(::LogoutUseCase)
    singleOf(::RegisterUseCase)
    singleOf(::SendVerificationEmailUseCase)
    singleOf(::SetUserAuthenticatedUseCase)
    singleOf(::VerifyEmailFormatUseCase)
}