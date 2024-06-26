package com.app.trialcryptowallet.data.model.domain

data class ItemCryptocurrencyInSellCryptocurrencyDialog(
    val id: String,
    val symbol: String,
    val name: String,
    val current_price: Double,
    val available_amount: Double
)