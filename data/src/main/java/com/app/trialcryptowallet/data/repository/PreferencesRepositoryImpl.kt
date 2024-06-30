package com.app.trialcryptowallet.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.app.trialcryptowallet.domain.repository.PreferencesRepository

class PreferencesRepositoryImpl(context: Context): PreferencesRepository {

    companion object {
        private const val PREFERENCES_FILE_KEY = "preferences"
        private const val PREFERENCES_KEY_AVAILABLE_BALANCE = "available_balance"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)

    override fun getAvailableBalance(): Double = sharedPreferences.getFloat(
        PREFERENCES_KEY_AVAILABLE_BALANCE, 0.0f).toDouble()
    override fun setAvailableBalance(balance: Double) = sharedPreferences.edit().putFloat(
        PREFERENCES_KEY_AVAILABLE_BALANCE, balance.toFloat()).apply()
}