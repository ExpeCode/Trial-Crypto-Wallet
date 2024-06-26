package com.app.trialcryptowallet.data.model.domain

import com.app.trialcryptowallet.data.network.Days

data class ItemHistoricalChartPeriod(@Days val days: Int) {
    var displayName: String = ""
}