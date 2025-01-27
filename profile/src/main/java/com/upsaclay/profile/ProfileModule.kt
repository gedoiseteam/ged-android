package com.upsaclay.profile

import com.upsaclay.profile.domain.usecase.LogoutUseCase
import com.upsaclay.profile.presentation.viewmodels.AccountViewModel
import com.upsaclay.profile.presentation.viewmodels.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AccountViewModel)

    singleOf(::LogoutUseCase)
}