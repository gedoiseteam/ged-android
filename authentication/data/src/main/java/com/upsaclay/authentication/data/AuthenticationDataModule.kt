package com.upsaclay.authentication.data

import com.upsaclay.authentication.data.local.AuthenticationLocalDataSource
import com.upsaclay.authentication.data.remote.AuthenticationRemoteDataSource
import com.upsaclay.authentication.data.remote.AuthenticationRetrofitApi
import com.upsaclay.authentication.data.remote.firebase.FirebaseAuthenticationApi
import com.upsaclay.authentication.data.remote.firebase.FirebaseAuthenticationApiImpl
import com.upsaclay.authentication.data.remote.firebase.FirebaseAuthenticationRemoteDataSource
import com.upsaclay.authentication.data.repository.AuthenticationRepositoryImpl
import com.upsaclay.authentication.data.repository.FirebaseAuthenticationRepositoryImpl
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val PARIS_SACLAY_SERVER_URL = "https://adonis-api.universite-paris-saclay.fr"
private const val PARIS_SACLAY_SERVER_QUALIFIER = "paris_saclay_qualifier"

val authenticationDataModule = module {
    single<Retrofit>(qualifier = named(PARIS_SACLAY_SERVER_QUALIFIER)) {
        Retrofit.Builder()
            .baseUrl(PARIS_SACLAY_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>(qualifier = named(PARIS_SACLAY_SERVER_QUALIFIER))
            .create(AuthenticationRetrofitApi::class.java)
    }

    singleOf(::AuthenticationRepositoryImpl) { bind<AuthenticationRepository>() }
    singleOf(::AuthenticationRemoteDataSource)
    singleOf(::AuthenticationLocalDataSource)

    singleOf(::FirebaseAuthenticationRepositoryImpl) { bind<FirebaseAuthenticationRepository>() }
    singleOf(::FirebaseAuthenticationApiImpl) { bind<FirebaseAuthenticationApi>() }
    singleOf(::FirebaseAuthenticationRemoteDataSource)
}