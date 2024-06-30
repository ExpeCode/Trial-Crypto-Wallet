package com.app.trialcryptowallet.domain.di

import com.app.trialcryptowallet.domain.usecase.GetCoinsListWithMarketDataUseCase
import com.app.trialcryptowallet.domain.usecase.GetCoinHistoricalChartDataByIdUseCase
import com.app.trialcryptowallet.domain.usecase.GetAllCryptocurrenciesInWalletUseCase
import com.app.trialcryptowallet.domain.usecase.FindCryptocurrencyInWalletByIdUseCase
import com.app.trialcryptowallet.domain.usecase.InsertCryptocurrencyInWalletUseCase
import com.app.trialcryptowallet.domain.usecase.DeleteCryptocurrencyInWalletUseCase
import com.app.trialcryptowallet.domain.usecase.UpdateCryptocurrencyInWalletUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetCoinsListWithMarketDataUseCase)
    factoryOf(::GetCoinHistoricalChartDataByIdUseCase)

    factoryOf(::GetAllCryptocurrenciesInWalletUseCase)
    factoryOf(::FindCryptocurrencyInWalletByIdUseCase)
    factoryOf(::InsertCryptocurrencyInWalletUseCase)
    factoryOf(::DeleteCryptocurrencyInWalletUseCase)
    factoryOf(::UpdateCryptocurrencyInWalletUseCase)
}