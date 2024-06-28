package com.app.trialcryptowallet.data.network

import com.app.trialcryptowallet.data.model.dto.CoinHistoricalChartData
import com.app.trialcryptowallet.data.model.dto.CryptocurrencyDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApiService {

    @GET("coins/markets")
    suspend fun getCoinsListWithMarketData(
        @Query("vs_currency") @Currency currency: String = USD
    ): Response<List<CryptocurrencyDto>>

    @GET("coins/{id}/market_chart")
    suspend fun getCoinHistoricalChartDataById(
        @Path("id") id: String,
        @Query("vs_currency") @Currency currency: String = USD,
        @Query("days") @Days days: Int,
        @Query("precision") precision: String = "full"
    ): Response<CoinHistoricalChartData>
}