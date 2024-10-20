package com.upsaclay.gedoise

import androidx.room.Room
import com.upsaclay.gedoise.data.GedoiseDatabase
import com.upsaclay.gedoise.presentation.MainViewModel
import com.upsaclay.gedoise.presentation.profile.ProfileViewModel
import com.upsaclay.gedoise.presentation.account.AccountViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
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
}