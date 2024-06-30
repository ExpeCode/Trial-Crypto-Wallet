package com.app.trialcryptowallet.domain.model.dto

data class CoinHistoricalChartData(
    val prices: List<List<Double>>,
    val market_caps: List<List<Double>>,
    val total_volumes: List<List<Double>>
)