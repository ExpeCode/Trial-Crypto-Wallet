package com.app.trialcryptowallet.data.model.domain

data class ItemCryptocurrencyInMarket(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double
)