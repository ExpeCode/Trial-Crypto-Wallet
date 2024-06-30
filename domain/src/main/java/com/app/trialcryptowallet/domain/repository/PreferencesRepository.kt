package com.app.trialcryptowallet.domain.repository

interface PreferencesRepository {
    fun getAvailableBalance(): Double
    fun setAvailableBalance(balance: Double)
}