package com.app.trialcryptowallet.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)

    fun getAvailableBalance(): Double = sharedPreferences.getFloat(Constants.PREFERENCES_KEY_AVAILABLE_BALANCE, 0.0f).toDouble()
    fun setAvailableBalance(balance: Double) = sharedPreferences.edit().putFloat(Constants.PREFERENCES_KEY_AVAILABLE_BALANCE, balance.toFloat()).apply()
}