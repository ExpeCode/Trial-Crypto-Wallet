package com.app.trialcryptowallet.data.repository

import com.app.trialcryptowallet.data.db.WalletDao
import com.app.trialcryptowallet.data.model.CryptocurrencyInWalletEntity
import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.WalletRepository

class WalletRepositoryImpl(private val walletDao: WalletDao) : WalletRepository {
    override suspend fun getAllCryptocurrenciesInWallet(): List<CryptocurrencyInWallet> {
        return walletDao.getAll().map {
            CryptocurrencyInWallet(
                id = it.id,
                name = it.name,
                amount = it.amount)
        }
    }

    override suspend fun findCryptocurrencyInWalletById(id: String): CryptocurrencyInWallet? {
        return walletDao.findById(id)?.let {
            CryptocurrencyInWallet(
                id = it.id,
                name = it.name,
                amount = it.amount)
        }
    }

    override suspend fun insertCryptocurrencyInWallet(asset: CryptocurrencyInWallet) {
        walletDao.insert(CryptocurrencyInWalletEntity(asset.id, asset.name, asset.amount))
    }

    override suspend fun deleteCryptocurrencyInWallet(asset: CryptocurrencyInWallet) {
        walletDao.delete(CryptocurrencyInWalletEntity(asset.id, asset.name, asset.amount))
    }

    override suspend fun updateCryptocurrencyInWallet(asset: CryptocurrencyInWallet) {
        walletDao.update(CryptocurrencyInWalletEntity(asset.id, asset.name, asset.amount))
    }
}