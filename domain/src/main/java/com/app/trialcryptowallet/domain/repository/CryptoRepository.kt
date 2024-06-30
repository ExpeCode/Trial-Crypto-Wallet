package com.app.trialcryptowallet.domain.repository

import com.app.trialcryptowallet.domain.model.common.Result
import com.app.trialcryptowallet.domain.model.dto.CoinHistoricalChartData
import com.app.trialcryptowallet.domain.model.dto.CryptocurrencyDto
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
    suspend fun getCoinsListWithMarketData(): Flow<Result<List<CryptocurrencyDto>>>
    suspend fun getCoinHistoricalChartDataById(id: String, days: Int): Flow<Result<CoinHistoricalChartData>>
}