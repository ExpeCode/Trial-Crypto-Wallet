package com.app.trialcryptowallet.domain.repository

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet

interface WalletRepository {
    suspend fun getAllCryptocurrenciesInWallet(): List<CryptocurrencyInWallet>
    suspend fun findCryptocurrencyInWalletById(id: String): CryptocurrencyInWallet?
    suspend fun insertCryptocurrencyInWallet(asset: CryptocurrencyInWallet)
    suspend fun deleteCryptocurrencyInWallet(asset: CryptocurrencyInWallet)
    suspend fun updateCryptocurrencyInWallet(asset: CryptocurrencyInWallet)
}