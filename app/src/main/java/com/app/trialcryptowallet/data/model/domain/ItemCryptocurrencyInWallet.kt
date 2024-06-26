package com.app.trialcryptowallet.data.model.domain

data class ItemCryptocurrencyInWallet(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val amount: Double
)