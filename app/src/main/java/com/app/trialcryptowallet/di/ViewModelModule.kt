package com.app.trialcryptowallet.di

import com.app.trialcryptowallet.screens.market.MarketViewModel
import com.app.trialcryptowallet.screens.wallet.WalletViewModel
import com.app.trialcryptowallet.screens.chart.ChartViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::WalletViewModel)
    viewModelOf(::MarketViewModel)
    viewModelOf(::ChartViewModel)
}