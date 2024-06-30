package com.app.trialcryptowallet.di

import com.app.trialcryptowallet.screens.wallet.WalletViewModel
import com.app.trialcryptowallet.screens.market.MarketViewModel
import com.app.trialcryptowallet.screens.chart.ChartViewModel
import com.app.trialcryptowallet.utils.ConnectivityMonitor
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::WalletViewModel)
    viewModelOf(::MarketViewModel)
    viewModelOf(::ChartViewModel)

    singleOf(::ConnectivityMonitor)
}