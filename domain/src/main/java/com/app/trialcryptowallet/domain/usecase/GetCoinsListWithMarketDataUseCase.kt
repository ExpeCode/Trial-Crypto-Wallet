package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.repository.CryptoRepository

class GetCoinsListWithMarketDataUseCase(private val cryptoRepository: CryptoRepository) {
    suspend operator fun invoke() = cryptoRepository.getCoinsListWithMarketData()
}