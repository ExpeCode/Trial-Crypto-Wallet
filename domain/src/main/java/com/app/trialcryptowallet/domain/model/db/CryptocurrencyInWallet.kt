package com.app.trialcryptowallet.domain.model.db

data class CryptocurrencyInWallet(
    val id: String,
    val name: String,
    var amount: Double
)