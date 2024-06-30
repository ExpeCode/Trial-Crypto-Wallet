package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.WalletRepository

class DeleteCryptocurrencyInWalletUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(asset: CryptocurrencyInWallet) = repository.deleteCryptocurrencyInWallet(asset)
}