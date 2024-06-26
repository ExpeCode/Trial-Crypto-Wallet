package com.app.trialcryptowallet.di

import com.app.trialcryptowallet.data.repository.Repository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::Repository)
}