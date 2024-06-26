package com.app.trialcryptowallet.data.model.dto

data class CryptocurrencyDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double
)