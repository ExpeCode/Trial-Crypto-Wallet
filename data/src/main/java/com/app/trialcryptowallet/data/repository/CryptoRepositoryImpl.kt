package com.app.trialcryptowallet.data.repository

import com.app.trialcryptowallet.data.api.CoinGeckoApiService
import com.app.trialcryptowallet.data.model.Days
import com.app.trialcryptowallet.domain.model.common.Result
import com.app.trialcryptowallet.domain.model.dto.CoinHistoricalChartData
import com.app.trialcryptowallet.domain.model.dto.CryptocurrencyDto
import com.app.trialcryptowallet.domain.repository.CryptoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

class CryptoRepositoryImpl(private val coinGeckoApiService: CoinGeckoApiService) : CryptoRepository {

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

    override suspend fun getCoinHistoricalChartDataById(id: String, @Days days: Int): Flow<Result<CoinHistoricalChartData>> = flow {
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
}