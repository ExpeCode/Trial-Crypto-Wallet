package com.app.trialcryptowallet.di

import com.app.trialcryptowallet.utils.ConnectivityMonitor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    singleOf(::ConnectivityMonitor)
}