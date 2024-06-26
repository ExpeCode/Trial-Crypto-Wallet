package com.app.trialcryptowallet

import android.app.Application
import com.app.trialcryptowallet.di.coinGeckoNetworkModule
import com.app.trialcryptowallet.di.databaseModule
import com.app.trialcryptowallet.di.networkModule
import com.app.trialcryptowallet.di.preferencesModule
import com.app.trialcryptowallet.di.repositoryModule
import com.app.trialcryptowallet.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                coinGeckoNetworkModule,
                databaseModule,
                networkModule,
                preferencesModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}