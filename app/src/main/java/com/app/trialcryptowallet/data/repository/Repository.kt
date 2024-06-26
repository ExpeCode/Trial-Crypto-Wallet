package com.app.trialcryptowallet.data.repository

import com.app.trialcryptowallet.data.PreferencesManager
import com.app.trialcryptowallet.data.db.WalletDao
import com.app.trialcryptowallet.data.model.Result
import com.app.trialcryptowallet.data.model.dto.CoinHistoricalChartData
import com.app.trialcryptowallet.data.model.dto.CryptocurrencyDto
import com.app.trialcryptowallet.data.model.entity.CryptocurrencyInWalletEntity
import com.app.trialcryptowallet.data.network.CoinGeckoApiService
import com.app.trialcryptowallet.data.network.Days
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

class Repository(
    private val coinGeckoApiService: CoinGeckoApiService,
    private val walletDao: WalletDao,
    private val preferencesManager: PreferencesManager) {

    suspend fun getCoinsListWithMarketData(): Flow<Result<List<CryptocurrencyDto>>> = flow {
        try {
            val response = coinGeckoApiService.getCoinsListWithMarketData()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    emit(Result.Success(body))
                } ?: emit(Result.Error(response.code(), IOException("Response body is null")))
            } else {
                emit(Result.Error(response.code(), HttpException(response)))
            }
        } catch (e: IOException) {
            emit(Result.Error(exception = e))
        } catch (e: HttpException) {
            emit(Result.Error(exception = e))
        }
    }.flowOn(Dispatchers.IO)
    suspend fun getCoinHistoricalChartDataById(id: String, @Days days: Int): Flow<Result<CoinHistoricalChartData>> = flow<Result<CoinHistoricalChartData>> {
        try {
            val response = coinGeckoApiService.getCoinHistoricalChartDataById(id = id, days = days)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    emit(Result.Success(body))
                } ?: emit(Result.Error(response.code(), IOException("Response body is null")))
            } else {
                emit(Result.Error(response.code(), HttpException(response)))
            }
        } catch (e: IOException) {
            emit(Result.Error(exception = e))
        } catch (e: HttpException) {
            emit(Result.Error(exception = e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getAllCryptocurrenciesInWallet(): List<CryptocurrencyInWalletEntity> {
        return walletDao.getAll()
    }
    suspend fun findCryptocurrencyInWalletById(id: String): CryptocurrencyInWalletEntity? {
        return walletDao.findById(id)
    }
    suspend fun insertCryptocurrencyInWallet(entity: CryptocurrencyInWalletEntity) {
        walletDao.insert(entity)
    }
    suspend fun deleteCryptocurrencyInWallet(entity: CryptocurrencyInWalletEntity) {
        walletDao.delete(entity)
    }
    suspend fun updateCryptocurrencyInWallet(entity: CryptocurrencyInWalletEntity) {
        walletDao.update(entity)
    }

    fun getAvailableBalance(): Double {
        return preferencesManager.getAvailableBalance()
    }
    fun setAvailableBalance(balance: Double) {
        preferencesManager.setAvailableBalance(balance)
    }
}