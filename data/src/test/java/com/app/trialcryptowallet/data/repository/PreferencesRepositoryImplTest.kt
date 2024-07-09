package com.app.trialcryptowallet.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.app.trialcryptowallet.domain.repository.PreferencesRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PreferencesRepositoryImplTest {

    private lateinit var preferencesRepository: PreferencesRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferencesRepository = PreferencesRepositoryImpl(context)
    }

    @Test
    fun testSetAndGetAvailableBalance() {
        val balance = 123.45
        preferencesRepository.setAvailableBalance(balance)
        val retrievedBalance = preferencesRepository.getAvailableBalance()
        assertEquals(balance, retrievedBalance, 0.0001)
    }
}
