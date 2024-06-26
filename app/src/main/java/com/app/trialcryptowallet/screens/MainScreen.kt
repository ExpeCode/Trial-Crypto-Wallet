package com.app.trialcryptowallet.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.trialcryptowallet.currentRoute
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInChartScreen
import com.app.trialcryptowallet.navigate
import com.app.trialcryptowallet.screens.chart.ChartScreen
import com.app.trialcryptowallet.ui.components.BottomNavigationBar
import com.app.trialcryptowallet.ui.theme.TrialCryptoWalletTheme
import com.app.trialcryptowallet.screens.market.MarketScreen
import com.app.trialcryptowallet.screens.wallet.WalletScreen

private const val KEY_ITEM_CRYPTOCURRENCY_IN_CHART_SCREEN = "itemCryptocurrencyInChartScreen"
private const val KEY_AMOUNT = "amount"

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentRoute = currentRoute(navController = navController)

    TrialCryptoWalletTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (isVisibleBottomBar(currentRoute)) {
                    val items = listOf(
                        Screen.Wallet,
                        Screen.Market
                    )
                    BottomNavigationBar(navController, currentRoute, items) { navController, id ->
                        navigate(navController, id)
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Wallet.id,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Screen.Wallet.id) {
                    WalletScreen(
                        openChart = { itemCryptocurrencyInChartScreen, amount ->
                            navController.currentBackStackEntry?.savedStateHandle?.apply {
                                set(key = KEY_ITEM_CRYPTOCURRENCY_IN_CHART_SCREEN, value = itemCryptocurrencyInChartScreen)
                                set(key = KEY_AMOUNT, value = amount)
                            }
                            navigate(navController, Screen.Chart.id)
                        },
                        openMarket = {
                            navigate(navController, Screen.Market.id)
                        }
                    )
                }
                composable(route = Screen.Market.id) {
                    MarketScreen(
                        openChart = { itemCryptocurrencyInChartScreen, amount ->
                            navController.currentBackStackEntry?.savedStateHandle?.apply {
                                set(key = KEY_ITEM_CRYPTOCURRENCY_IN_CHART_SCREEN, value = itemCryptocurrencyInChartScreen)
                                set(key = KEY_AMOUNT, value = amount)
                            }
                            navigate(navController, Screen.Chart.id)
                        },
                        openWallet = {
                            navigate(navController, Screen.Wallet.id)
                        }
                    )
                }
                composable(route = Screen.Chart.id) {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<ItemCryptocurrencyInChartScreen>(KEY_ITEM_CRYPTOCURRENCY_IN_CHART_SCREEN)
                        ?.let { itemCryptocurrencyInChartScreen ->
                            val amount = navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<Double>(KEY_AMOUNT) ?: 0.0
                            ChartScreen(
                                newItemCryptocurrencyInChartScreen = itemCryptocurrencyInChartScreen,
                                newAmount = amount,
                                onBack = {
                                    navController.popBackStack()
                                },
                                openWallet = {
                                    navigate(navController, Screen.Wallet.id)
                                }
                            )
                        }
                }
            }
        }

        BackHandler {
            if (currentRoute == Screen.Wallet.id) {
                // Exit the app if on Wallet screen
                (navController.context as? Activity)?.finish()
            } else {
                // Navigate back to the previous screen
                navController.popBackStack()
            }
        }
    }
}

private fun isVisibleBottomBar(currentRoute: String?) = currentRoute == Screen.Wallet.id || currentRoute == Screen.Market.id