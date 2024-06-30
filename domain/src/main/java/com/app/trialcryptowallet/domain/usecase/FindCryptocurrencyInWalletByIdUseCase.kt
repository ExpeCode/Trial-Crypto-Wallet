package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.WalletRepository

class FindCryptocurrencyInWalletByIdUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(id: String): CryptocurrencyInWallet? = repository.findCryptocurrencyInWalletById(id)
}