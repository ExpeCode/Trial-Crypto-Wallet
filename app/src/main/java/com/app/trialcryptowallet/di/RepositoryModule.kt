package com.app.trialcryptowallet.di

import com.app.trialcryptowallet.data.repository.Repository
import com.app.trialcryptowallet.data.repository.RepositoryInterface
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::Repository) { bind<RepositoryInterface>() }
}