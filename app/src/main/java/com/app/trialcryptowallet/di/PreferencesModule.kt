package com.app.trialcryptowallet.di

import com.app.trialcryptowallet.data.PreferencesManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferencesModule = module {
    singleOf(::PreferencesManager)
}