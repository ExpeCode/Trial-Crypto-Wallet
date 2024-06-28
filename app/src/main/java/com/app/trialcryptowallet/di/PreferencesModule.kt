package com.app.trialcryptowallet.di

import com.app.trialcryptowallet.data.PreferencesManager
import com.app.trialcryptowallet.data.PreferencesManagerInterface
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferencesModule = module {
    singleOf(::PreferencesManager) { bind<PreferencesManagerInterface>() }
}