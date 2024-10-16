package com.upsaclay.common

import com.upsaclay.common.domain.usecase.DeleteUserProfilePictureUseCase
import com.upsaclay.common.domain.usecase.GetAllUserUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.GetDrawableUriUseCase
import com.upsaclay.common.domain.usecase.GetUserUseCase
import com.upsaclay.common.domain.usecase.IsUserExistUseCase
import com.upsaclay.common.domain.usecase.SetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.UpdateUserProfilePictureUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {

    singleOf(::DeleteUserProfilePictureUseCase)
    singleOf(::GetAllUserUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::GetDrawableUriUseCase)
    singleOf(::GetUserUseCase)
    singleOf(::IsUserExistUseCase)
    singleOf(::SetCurrentUserUseCase)
    singleOf(::UpdateUserProfilePictureUseCase)
}