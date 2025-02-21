package com.upsaclay.news.domain

import com.upsaclay.common.domain.e
import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementFlowUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.RecreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.UpdateAnnouncementUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val BACKGROUND_SCOPE = named("BackgroundScope")

val newsDomainModule = module {
    single<CoroutineScope>(BACKGROUND_SCOPE) {
        CoroutineScope(
    SupervisorJob() +
            Dispatchers.IO +
            CoroutineExceptionHandler { coroutineContext, throwable ->
                e("Uncaught error in backgroundScope", throwable)
            }
        )
    }


    single {
        CreateAnnouncementUseCase(
            announcementRepository = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }

    single {
        DeleteAnnouncementUseCase(
            announcementRepository = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }

    singleOf(::GetAnnouncementFlowUseCase)
    singleOf(::GetAnnouncementsUseCase)
    singleOf(::GetAnnouncementUseCase)
    single {
        RecreateAnnouncementUseCase(
            announcementRepository = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }
    singleOf(::RefreshAnnouncementsUseCase)
    singleOf(::UpdateAnnouncementUseCase)
}