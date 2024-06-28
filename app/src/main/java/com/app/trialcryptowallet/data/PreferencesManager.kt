package com.app.trialcryptowallet.data

import android.content.Context
import android.content.SharedPreferences

private const val PREFERENCES_FILE_KEY = "preferences"
private const val PREFERENCES_KEY_AVAILABLE_BALANCE = "available_balance"

class PreferencesManager(context: Context): PreferencesManagerInterface {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)

    override fun getAvailableBalance(): Double = sharedPreferences.getFloat(PREFERENCES_KEY_AVAILABLE_BALANCE, 0.0f).toDouble()
    override fun setAvailableBalance(balance: Double) = sharedPreferences.edit().putFloat(PREFERENCES_KEY_AVAILABLE_BALANCE, balance.toFloat()).apply()
}