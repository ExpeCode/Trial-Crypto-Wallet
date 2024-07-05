package com.app.trialcryptowallet.domain.repository

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet

class FakeWalletRepository : WalletRepository {

    private val cryptocurrencies = mutableListOf<CryptocurrencyInWallet>()

    override suspend fun getAllCryptocurrenciesInWallet(): List<CryptocurrencyInWallet> {
        return cryptocurrencies
    }

    override suspend fun insertCryptocurrencyInWallet(asset: CryptocurrencyInWallet) {
        cryptocurrencies.add(asset)
    }

    override suspend fun deleteCryptocurrencyInWallet(asset: CryptocurrencyInWallet) {
        cryptocurrencies.remove(asset)
    }

    override suspend fun updateCryptocurrencyInWallet(asset: CryptocurrencyInWallet) {
        val index = cryptocurrencies.indexOfFirst { it.id == asset.id }
        if (index != -1) {
            cryptocurrencies[index] = asset
        }
    }

    override suspend fun findCryptocurrencyInWalletById(id: String): CryptocurrencyInWallet? {
        return cryptocurrencies.find { it.id == id }
    }
}
