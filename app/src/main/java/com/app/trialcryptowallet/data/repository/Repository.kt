package com.app.trialcryptowallet.data.repository

import com.app.trialcryptowallet.data.PreferencesManagerInterface
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
    private val preferencesManager: PreferencesManagerInterface
): RepositoryInterface {

    override suspend fun getCoinsListWithMarketData(): Flow<Result<List<CryptocurrencyDto>>> = flow {
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
    override suspend fun getCoinHistoricalChartDataById(id: String, @Days days: Int): Flow<Result<CoinHistoricalChartData>> = flow<Result<CoinHistoricalChartData>> {
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

    override suspend fun getAllCryptocurrenciesInWallet(): List<CryptocurrencyInWalletEntity> {
        return walletDao.getAll()
    }
    override suspend fun findCryptocurrencyInWalletById(id: String): CryptocurrencyInWalletEntity? {
        return walletDao.findById(id)
    }
    override suspend fun insertCryptocurrencyInWallet(entity: CryptocurrencyInWalletEntity) {
        walletDao.insert(entity)
    }
    override suspend fun deleteCryptocurrencyInWallet(entity: CryptocurrencyInWalletEntity) {
        walletDao.delete(entity)
    }
    override suspend fun updateCryptocurrencyInWallet(entity: CryptocurrencyInWalletEntity) {
        walletDao.update(entity)
    }

    override fun getAvailableBalance(): Double {
        return preferencesManager.getAvailableBalance()
    }
    override fun setAvailableBalance(balance: Double) {
        preferencesManager.setAvailableBalance(balance)
    }
}