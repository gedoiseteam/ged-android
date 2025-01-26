package com.upsaclay.profile

import com.upsaclay.profile.presentation.viewmodels.AccountViewModel
import com.upsaclay.profile.presentation.viewmodels.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AccountViewModel)
}