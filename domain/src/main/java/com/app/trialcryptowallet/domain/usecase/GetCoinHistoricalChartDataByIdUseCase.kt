package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.model.common.Days
import com.app.trialcryptowallet.domain.repository.CryptoRepository

class GetCoinHistoricalChartDataByIdUseCase(private val cryptoRepository: CryptoRepository) {
    suspend operator fun invoke(id: String, days: Days) = cryptoRepository.getCoinHistoricalChartDataById(id, days.value)
}