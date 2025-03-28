package com.upsaclay.authentication.domain

import com.upsaclay.authentication.domain.usecase.GenerateHashUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authenticationDomainModule = module {
    singleOf(::GenerateHashUseCase)
}