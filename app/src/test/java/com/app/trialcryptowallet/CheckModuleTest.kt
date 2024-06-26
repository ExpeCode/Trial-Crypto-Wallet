package com.app.trialcryptowallet

import com.app.trialcryptowallet.di.coinGeckoNetworkModule
import com.app.trialcryptowallet.di.databaseModule
import com.app.trialcryptowallet.di.networkModule
import com.app.trialcryptowallet.di.preferencesModule
import com.app.trialcryptowallet.di.repositoryModule
import com.app.trialcryptowallet.di.viewModelModule
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class CheckModulesTest : KoinTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
        coinGeckoNetworkModule.verify()
        databaseModule.verify()
        networkModule.verify()
        preferencesModule.verify()
        repositoryModule.verify()
        viewModelModule.verify()
    }
}