package com.app.trialcryptowallet.data.di

import com.app.trialcryptowallet.data.repository.CryptoRepositoryImpl
import com.app.trialcryptowallet.data.repository.PreferencesRepositoryImpl
import com.app.trialcryptowallet.data.repository.WalletRepositoryImpl
import com.app.trialcryptowallet.domain.repository.CryptoRepository
import com.app.trialcryptowallet.domain.repository.PreferencesRepository
import com.app.trialcryptowallet.domain.repository.WalletRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::provideLoggingInterceptor)
    singleOf(::provideHttpClient)
    singleOf(::provideRetrofit)
    singleOf(::provideService)

    singleOf(::provideDatabase)
    singleOf(::provideDao)

    singleOf(::PreferencesRepositoryImpl) { bind<PreferencesRepository>() }
    singleOf(::CryptoRepositoryImpl) { bind<CryptoRepository>() }
    singleOf(::WalletRepositoryImpl) { bind<WalletRepository>() }
}