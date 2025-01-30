package com.upsaclay.news

import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.UpdateAnnouncementUseCase
import com.upsaclay.news.presentation.viewmodels.CreateAnnouncementViewModel
import com.upsaclay.news.presentation.viewmodels.EditAnnouncementViewModel
import com.upsaclay.news.presentation.viewmodels.NewsViewModel
import com.upsaclay.news.presentation.viewmodels.ReadAnnouncementViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val newsModule = module {
    viewModelOf(::NewsViewModel)
    viewModelOf(::CreateAnnouncementViewModel)
    viewModel { (announcementId: String) ->
        ReadAnnouncementViewModel(
            announcementId = announcementId,
            getCurrentUserUseCase = get(),
            getAnnouncementUseCase = get(),
            getAnnouncementsUseCase = get(),
            deleteAnnouncementUseCase = get(),
        )
    }
    viewModel { (announcementId: String) ->
        EditAnnouncementViewModel(
            announcementId = announcementId,
            getAnnouncementUseCase = get(),
            updateAnnouncementUseCase = get()
        )
    }

    singleOf(::CreateAnnouncementUseCase)
    singleOf(::DeleteAnnouncementUseCase)
    singleOf(::GetAnnouncementsUseCase)
    singleOf(::GetAnnouncementUseCase)
    singleOf(::RefreshAnnouncementsUseCase)
    singleOf(::UpdateAnnouncementUseCase)
}