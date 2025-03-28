package com.upsaclay.news

import com.upsaclay.news.presentation.viewmodels.CreateAnnouncementViewModel
import com.upsaclay.news.presentation.viewmodels.EditAnnouncementViewModel
import com.upsaclay.news.presentation.viewmodels.NewsViewModel
import com.upsaclay.news.presentation.viewmodels.ReadAnnouncementViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val newsModule = module {
    viewModelOf(::NewsViewModel)
    viewModelOf(::CreateAnnouncementViewModel)
    viewModel { (announcementId: String) ->
        ReadAnnouncementViewModel(
            announcementId = announcementId,
            getAnnouncementFlowUseCase = get(),
            deleteAnnouncementUseCase = get(),
            userRepository = get(),
            announcementRepository = get()
        )
    }
    viewModel { (announcementId: String) ->
        EditAnnouncementViewModel(
            announcementId = announcementId,
            announcementRepository = get()
        )
    }
}