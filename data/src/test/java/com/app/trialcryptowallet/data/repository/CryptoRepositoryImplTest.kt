package com.app.trialcryptowallet.data.repository

import com.app.trialcryptowallet.data.api.CoinGeckoApiService
import com.app.trialcryptowallet.data.di.provideHttpClient
import com.app.trialcryptowallet.data.di.provideLoggingInterceptor
import com.app.trialcryptowallet.data.di.provideRetrofit
import com.app.trialcryptowallet.data.di.provideService
import com.app.trialcryptowallet.data.model.DAY
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CryptoRepositoryImplTest {

    private lateinit var apiService: CoinGeckoApiService
    private lateinit var cryptoRepository: CryptoRepositoryImpl

    @Before
    fun setup() {
        apiService = provideService(provideRetrofit(provideHttpClient(provideLoggingInterceptor())))
        cryptoRepository = CryptoRepositoryImpl(apiService)
    }

    @Test
    fun `test getCoinsListWithMarketData success`() = runBlocking {
        cryptoRepository.getCoinsListWithMarketData().collect { result ->
            assertTrue(result is com.app.trialcryptowallet.domain.model.common.Result.Success)
        }
    }

    @Test
    fun `test getCoinHistoricalChartDataById success`() = runBlocking {
        cryptoRepository.getCoinHistoricalChartDataById("bitcoin", DAY).collect { result ->
            assertTrue(result is com.app.trialcryptowallet.domain.model.common.Result.Success)
        }
    }
}