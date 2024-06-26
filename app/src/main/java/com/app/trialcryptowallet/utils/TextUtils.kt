package com.app.trialcryptowallet.utils

import com.app.trialcryptowallet.data.model.domain.ItemHistoricalChartData
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(amount)
}

fun formatToEightDecimals(value: Double): String {
    return "%.8f".format(value)
}

fun formatPriceChange(data: List<ItemHistoricalChartData>): String {
    if (data.isEmpty()) return ""

    val initialPrice = data.first().price
    val currentPrice = data.last().price
    val changeAmount = currentPrice - initialPrice
    val changePercentage = (changeAmount / initialPrice) * 100.0
    val sign = if (changeAmount >= 0) "+" else "-"

    val formattedChangeAmount = formatCurrency(abs(changeAmount))//String.format(Locale.getDefault(), "%,.2f", abs(changeAmount))
    val formattedChangePercentage = String.format(Locale.getDefault(), "%.2f", abs(changePercentage))

    return "$sign$formattedChangeAmount (${formattedChangePercentage}%)"
}