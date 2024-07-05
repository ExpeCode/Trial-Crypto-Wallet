package com.app.trialcryptowallet.data.db

import com.app.trialcryptowallet.data.model.CryptocurrencyInWalletEntity

class FakeWalletDao : WalletDao {

    private val cryptocurrencies = mutableListOf<CryptocurrencyInWalletEntity>()

    override suspend fun getAll(): List<CryptocurrencyInWalletEntity> {
        return cryptocurrencies
    }

    override suspend fun findById(id: String): CryptocurrencyInWalletEntity? {
        return cryptocurrencies.find {
            it.id == id
        }
    }

    override suspend fun insert(asset: CryptocurrencyInWalletEntity) {
        cryptocurrencies.add(asset)
    }

    override suspend fun delete(asset: CryptocurrencyInWalletEntity) {
        cryptocurrencies.remove(asset)
    }

    override suspend fun update(asset: CryptocurrencyInWalletEntity) {
        val index = cryptocurrencies.indexOfFirst { it.id == asset.id }
        if (index != -1) {
            cryptocurrencies[index] = asset
        }
    }
}