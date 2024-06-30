package com.app.trialcryptowallet

import android.app.Application
import com.app.trialcryptowallet.data.di.dataModule
import com.app.trialcryptowallet.di.appModule
import com.app.trialcryptowallet.domain.di.domainModule
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
                appModule,
                domainModule,
                dataModule
            )
        }
    }
}