package com.app.trialcryptowallet.data.model.domain

import com.app.trialcryptowallet.domain.model.common.Days

data class ItemHistoricalChartPeriod(val days: Days) {
    var displayName: String = ""
}