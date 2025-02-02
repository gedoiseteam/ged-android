package com.upsaclay.gedoise

import androidx.room.Room
import com.upsaclay.gedoise.data.GedoiseDatabase
import com.upsaclay.gedoise.domain.usecase.DeleteLocalDataUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import com.upsaclay.gedoise.presentation.viewmodels.AccountViewModel
import com.upsaclay.gedoise.presentation.viewmodels.MainViewModel
import com.upsaclay.gedoise.presentation.viewmodels.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private const val DATABASE_NAME = "GedoiseDatabase"

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            GedoiseDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    single { get<GedoiseDatabase>().announcementDao() }
    single { get<GedoiseDatabase>().conversationDao() }
    single { get<GedoiseDatabase>().messageDao() }

    viewModelOf(::MainViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AccountViewModel)

    singleOf(::DeleteLocalDataUseCase)
    singleOf(::StartListeningDataUseCase)
    singleOf(::StopListeningDataUseCase)
}