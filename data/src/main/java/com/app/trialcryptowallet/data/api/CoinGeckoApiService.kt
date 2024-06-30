package com.app.trialcryptowallet.data.api

import com.app.trialcryptowallet.data.model.Currency
import com.app.trialcryptowallet.data.model.Days
import com.app.trialcryptowallet.data.model.USD
import com.app.trialcryptowallet.domain.model.dto.CoinHistoricalChartData
import com.app.trialcryptowallet.domain.model.dto.CryptocurrencyDto
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