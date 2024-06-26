package com.app.trialcryptowallet.screens.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.app.trialcryptowallet.R
import com.app.trialcryptowallet.data.model.Error
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInChartScreen
import com.app.trialcryptowallet.data.model.domain.ItemHistoricalChartPeriod
import com.app.trialcryptowallet.data.network.DAY
import com.app.trialcryptowallet.data.network.HALF_YEAR
import com.app.trialcryptowallet.data.network.MONTH
import com.app.trialcryptowallet.data.network.WEEK
import com.app.trialcryptowallet.data.network.YEAR
import com.app.trialcryptowallet.ui.components.PriceChart
import com.app.trialcryptowallet.ui.components.TopAppBarSmall
import com.app.trialcryptowallet.ui.dialogs.BuyCryptocurrencyDialog
import com.app.trialcryptowallet.ui.dialogs.SellCryptocurrencyDialog
import com.app.trialcryptowallet.utils.formatCurrency
import com.app.trialcryptowallet.utils.formatPriceChange
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
@Composable
fun ChartScreen(
    chartViewModel: ChartViewModel = koinViewModel(),
    newItemCryptocurrencyInChartScreen: ItemCryptocurrencyInChartScreen = ItemCryptocurrencyInChartScreen("bitcoin", "btc", "Bitcoin", 64000.0),
    newAmount: Double = 0.5,
    onBack: () -> Unit = {},
    openWallet: () -> Unit = {}
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val itemCryptocurrencyInChartScreen by chartViewModel.itemCryptocurrencyInChartScreen.collectAsState()
    val chartPeriod by chartViewModel.chartPeriod.collectAsState()
    val historicalChartData by chartViewModel.historicalChartData.collectAsState()
    val amount by chartViewModel.amount.collectAsState()
    val itemCryptocurrencyInBuyCryptocurrencyDialog by chartViewModel.itemCryptocurrencyInBuyCryptocurrencyDialog.collectAsState()
    val itemCryptocurrencyInSellCryptocurrencyDialog by chartViewModel.itemCryptocurrencyInSellCryptocurrencyDialog.collectAsState()

    val isProfitable = if (historicalChartData.isNotEmpty()) {
        historicalChartData.last().price >= historicalChartData.first().price
    } else false
    val colorGreen = Color(0f, 0.5f, 0f, 1f)
    val colorRed = Color(0.5f, 0f, 0f, 1f)
    val itemsHistoricalChartPeriod = listOf(
        ItemHistoricalChartPeriod(DAY).apply { displayName = stringResource(R.string.day) },
        ItemHistoricalChartPeriod(WEEK).apply { displayName = stringResource(R.string.week) },
        ItemHistoricalChartPeriod(MONTH).apply { displayName = stringResource(R.string.month) },
        ItemHistoricalChartPeriod(HALF_YEAR).apply { displayName = stringResource(R.string.half_year) },
        ItemHistoricalChartPeriod(YEAR).apply { displayName = stringResource(R.string.year) }
    )

    val errorLoadingDataMessage = stringResource(R.string.error_loading_data)
    val errorRateLimitMessage = stringResource(R.string.error_rate_limit)
    val retryLabel = stringResource(R.string.retry)

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            chartViewModel.setCryptocurrency(newItemCryptocurrencyInChartScreen, newAmount)
            chartViewModel.fetchHistoricalChartData()
            chartViewModel.snackBarErrorMessage.collect { error ->
                val result = snackBarHostState.showSnackbar(
                    message = when (error) {
                        is Error.ErrorLoadingData -> errorLoadingDataMessage
                        is Error.ErrorRateLimit -> errorRateLimitMessage },
                    actionLabel = retryLabel,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    chartViewModel.fetchHistoricalChartData()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            TopAppBarSmall(title = itemCryptocurrencyInChartScreen?.name ?: "", onBack = onBack)

            itemCryptocurrencyInChartScreen?.let { itemCryptocurrencyInChartScreen ->
                // Current price
                Text(
                    text = formatCurrency(itemCryptocurrencyInChartScreen.current_price),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Period selection buttons
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    itemsIndexed(itemsHistoricalChartPeriod) { index, period ->
                        Button(
                            onClick = {
                                chartViewModel.setSelectedPeriod(period)
                                chartViewModel.fetchHistoricalChartData()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (chartPeriod.days == period.days) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(period.displayName)
                        }
                        if (index < itemsHistoricalChartPeriod.size - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }

                // Price change
                Text(
                    text = formatPriceChange(historicalChartData),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isProfitable) colorGreen else colorRed,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Price chart
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    PriceChart(
                        data = historicalChartData,
                        isProfitable = isProfitable,
                        isDateWithTime = chartPeriod.days <= 90
                    )
                }

                // Available amount
                Text(
                    text = stringResource(R.string.in_wallet, amount, itemCryptocurrencyInChartScreen.symbol.uppercase()),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Buy and sell buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            chartViewModel.onClickToBuy(itemCryptocurrencyInChartScreen)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(end = 4.dp)
                    ) {
                        Text(stringResource(R.string.buy_title))
                    }
                    if (amount > 0) {
                        Button(
                            onClick = {
                                chartViewModel.onClickToSell(itemCryptocurrencyInChartScreen)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .padding(start = 4.dp)
                        ) {
                            Text(stringResource(R.string.sell_title))
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    itemCryptocurrencyInBuyCryptocurrencyDialog?.let { cryptocurrency ->
        BuyCryptocurrencyDialog(
            itemCryptocurrencyInBuyCryptocurrencyDialog = cryptocurrency,
            balance = chartViewModel.getAvailableBalance(),
            onDismiss = {
                chartViewModel.onDismissBuyCryptocurrencyDialog()
            },
            onBuy = { amount, cost ->
                chartViewModel.buyCryptocurrency(cryptocurrency, amount, cost) {
                    openWallet.invoke()
                }
                chartViewModel.onDismissBuyCryptocurrencyDialog()
            }
        )
    }

    itemCryptocurrencyInSellCryptocurrencyDialog?.let { cryptocurrency ->
        SellCryptocurrencyDialog(
            itemCryptocurrencyInSellCryptocurrencyDialog = cryptocurrency,
            onDismiss = {
                chartViewModel.onDismissSellCryptocurrencyDialog()
            },
            onSell = { amount, price ->
                chartViewModel.sellCryptocurrency(cryptocurrency, amount, price) {
                    openWallet.invoke()
                }
                chartViewModel.onDismissSellCryptocurrencyDialog()
            }
        )
    }
}