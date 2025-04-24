package com.upsaclay.news.data

import com.upsaclay.common.data.GED_SERVER_QUALIFIER
import com.upsaclay.common.domain.e
import com.upsaclay.news.data.local.AnnouncementLocalDataSource
import com.upsaclay.news.data.remote.AnnouncementRemoteDataSource
import com.upsaclay.news.data.remote.api.AnnouncementApi
import com.upsaclay.news.data.repository.AnnouncementRepositoryImpl
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private val BACKGROUND_SCOPE = named("BackgroundScope")

val newsDataModule = module {
    single {
        get<Retrofit>(GED_SERVER_QUALIFIER)
            .create(AnnouncementApi::class.java)
    }

    single<CoroutineScope>(BACKGROUND_SCOPE) {
        CoroutineScope(
    SupervisorJob() +
            Dispatchers.IO +
            CoroutineExceptionHandler { coroutineContext, throwable ->
                e("Uncaught error in backgroundScope", throwable)
            }
        )
    }

    single<AnnouncementRepository> {
        AnnouncementRepositoryImpl(
            announcementRemoteDataSource = get(),
            announcementLocalDataSource = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }
    singleOf(::AnnouncementRemoteDataSource)
    singleOf(::AnnouncementLocalDataSource)
}