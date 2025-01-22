package com.upsaclay.news

import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.usecase.ConvertAnnouncementToJsonUseCase
import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.UpdateAnnouncementUseCase
import com.upsaclay.news.presentation.viewmodel.AnnouncementViewModel
import com.upsaclay.news.presentation.viewmodel.CreateAnnouncementViewModel
import com.upsaclay.news.presentation.viewmodel.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val newsModule = module {
    viewModelOf(::NewsViewModel)
    viewModelOf(::CreateAnnouncementViewModel)
    viewModel { (announcement: Announcement) ->
        AnnouncementViewModel(
            announcement = announcement,
            updateAnnouncementUseCase = get(),
            deleteAnnouncementUseCase = get(),
            getCurrentUserUseCase = get()
        )
    }

    singleOf(::ConvertAnnouncementToJsonUseCase)
    singleOf(::CreateAnnouncementUseCase)
    singleOf(::DeleteAnnouncementUseCase)
    singleOf(::GetAnnouncementsUseCase)
    singleOf(::RefreshAnnouncementsUseCase)
    singleOf(::UpdateAnnouncementUseCase)
}