package com.upsaclay.common

import com.upsaclay.common.domain.usecase.DeleteProfilePictureUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.GetDrawableUriUseCase
import com.upsaclay.common.domain.usecase.GetUserUseCase
import com.upsaclay.common.domain.usecase.GetUsersUseCase
import com.upsaclay.common.domain.usecase.IsUserExistUseCase
import com.upsaclay.common.domain.usecase.SetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.UpdateUserProfilePictureUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {

    singleOf(::DeleteProfilePictureUseCase)
    singleOf(::GetUsersUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::GetDrawableUriUseCase)
    singleOf(::GetUserUseCase)
    singleOf(::IsUserExistUseCase)
    singleOf(::SetCurrentUserUseCase)
    singleOf(::UpdateUserProfilePictureUseCase)
}