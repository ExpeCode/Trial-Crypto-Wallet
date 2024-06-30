package com.app.trialcryptowallet.data.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemCryptocurrencyInChartScreen(
    val id: String,
    val symbol: String,
    val name: String,
    val current_price: Double
) : Parcelable