package com.app.trialcryptowallet.screens

import com.app.trialcryptowallet.R

sealed class Screen(val id: String, val iconResId: Int, val titleResId: Int) {
    data object Wallet : Screen("wallet", R.drawable.ic_account_balance_wallet, R.string.wallet)
    data object Market : Screen("market", R.drawable.ic_shopping_cart, R.string.market)
    data object Chart : Screen("chart", 0, 0)
}