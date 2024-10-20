package com.upsaclay.news

import com.upsaclay.news.domain.usecase.ConvertAnnouncementToJsonUseCase
import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAllAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.UpdateAnnouncementUseCase
import com.upsaclay.news.presentation.viewmodel.CreateAnnouncementViewModel
import com.upsaclay.news.presentation.viewmodel.EditAnnouncementViewModel
import com.upsaclay.news.presentation.viewmodel.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val newsModule = module {

    viewModelOf(::NewsViewModel)
    viewModel { (announcement: com.upsaclay.news.domain.model.Announcement) ->
        EditAnnouncementViewModel(
            editedAnnouncement = announcement,
            updateAnnouncementUseCase = get()
        )
    }
    viewModelOf(::CreateAnnouncementViewModel)

    singleOf(::ConvertAnnouncementToJsonUseCase)
    singleOf(::CreateAnnouncementUseCase)
    singleOf(::DeleteAnnouncementUseCase)
    singleOf(::GetAllAnnouncementsUseCase)
    singleOf(::GetAnnouncementUseCase)
    singleOf(::RefreshAnnouncementsUseCase)
    singleOf(::UpdateAnnouncementUseCase)
}