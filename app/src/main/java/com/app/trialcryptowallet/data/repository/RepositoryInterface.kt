package com.app.trialcryptowallet.data.repository

import com.app.trialcryptowallet.data.model.Result
import com.app.trialcryptowallet.data.model.dto.CoinHistoricalChartData
import com.app.trialcryptowallet.data.model.dto.CryptocurrencyDto
import com.app.trialcryptowallet.data.model.entity.CryptocurrencyInWalletEntity
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getCoinsListWithMarketData(): Flow<Result<List<CryptocurrencyDto>>>
    suspend fun getCoinHistoricalChartDataById(id: String, days: Int): Flow<Result<CoinHistoricalChartData>>
    suspend fun getAllCryptocurrenciesInWallet(): List<CryptocurrencyInWalletEntity>
    suspend fun findCryptocurrencyInWalletById(id: String): CryptocurrencyInWalletEntity?
    suspend fun insertCryptocurrencyInWallet(entity: CryptocurrencyInWalletEntity)
    suspend fun deleteCryptocurrencyInWallet(entity: CryptocurrencyInWalletEntity)
    suspend fun updateCryptocurrencyInWallet(entity: CryptocurrencyInWalletEntity)
    fun getAvailableBalance(): Double
    fun setAvailableBalance(balance: Double)
}