package com.app.trialcryptowallet

import com.app.trialcryptowallet.data.di.dataModule
import com.app.trialcryptowallet.di.appModule
import com.app.trialcryptowallet.domain.di.domainModule
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class CheckModulesTest : KoinTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
        appModule.verify()
        domainModule.verify()
        dataModule.verify()
    }
}