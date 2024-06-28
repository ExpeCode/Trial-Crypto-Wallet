package com.app.trialcryptowallet.data

interface PreferencesManagerInterface {
    fun getAvailableBalance(): Double
    fun setAvailableBalance(balance: Double)
}