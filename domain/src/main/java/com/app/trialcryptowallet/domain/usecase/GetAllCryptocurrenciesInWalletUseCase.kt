package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.WalletRepository

class GetAllCryptocurrenciesInWalletUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(): List<CryptocurrencyInWallet> = repository.getAllCryptocurrenciesInWallet()
}