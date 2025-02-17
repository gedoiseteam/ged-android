package com.upsaclay.authentication.domain

import com.upsaclay.authentication.domain.usecase.GenerateHashUseCase
import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.authentication.domain.usecase.LoginUseCase
import com.upsaclay.authentication.domain.usecase.LogoutUseCase
import com.upsaclay.authentication.domain.usecase.RegisterUseCase
import com.upsaclay.authentication.domain.usecase.SendVerificationEmailUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authenticationDomainModule = module {
    singleOf(::CreateUserUseCase)
    singleOf(::GenerateHashUseCase)
    singleOf(::IsUserAuthenticatedUseCase)
    singleOf(::IsEmailVerifiedUseCase)
    singleOf(::LoginUseCase)
    singleOf(::LogoutUseCase)
    singleOf(::RegisterUseCase)
    singleOf(::SendVerificationEmailUseCase)
    singleOf(::SetUserAuthenticatedUseCase)
}