package com.app.trialcryptowallet.screens.market

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.app.trialcryptowallet.R
import com.app.trialcryptowallet.domain.model.common.Error
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInChartScreen
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInMarket
import com.app.trialcryptowallet.ui.dialogs.BuyCryptocurrencyDialog
import com.app.trialcryptowallet.utils.formatCurrency
import com.app.trialcryptowallet.utils.shimmerBackground
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MarketScreen(
    marketViewModel: MarketViewModel = koinViewModel(),
    openChart: (ItemCryptocurrencyInChartScreen, Double) -> Unit = { _, _ -> },
    openWallet: () -> Unit = {}
) {

    val isLoading by marketViewModel.isLoading.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val cryptocurrencies by marketViewModel.cryptocurrencies.collectAsState()
    val itemCryptocurrencyInBuyCryptocurrencyDialog by marketViewModel.itemCryptocurrencyInBuyCryptocurrencyDialog.collectAsState()

    /*val isLoading by remember {
        mutableStateOf(false)
    }
    val cryptocurrencies = remember {
        mutableListOf<ItemCryptocurrencyInMarket>().apply {
            add(ItemCryptocurrencyInMarket("eth", "ETH", "Etherium", "", 12999.4))
            add(ItemCryptocurrencyInMarket("btc", "BTC", "Bitcoin", "", 64000.4))
        }
    }
    val availableBalance by remember { mutableDoubleStateOf(100.0) }
    val itemCryptocurrencyInBuyCryptocurrencyDialog by remember {
        //mutableStateOf<ItemCryptocurrencyInBuyCryptocurrencyDialog?>(null)
        mutableStateOf(ItemCryptocurrencyInBuyCryptocurrencyDialog("eth","Etherium",12999.4))
    }*/

    val errorLoadingDataMessage = stringResource(R.string.error_loading_data)
    val errorRateLimitMessage = stringResource(R.string.error_rate_limit)
    val retryLabel = stringResource(R.string.retry)

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            marketViewModel.refreshCryptocurrenciesInMarket()
            marketViewModel.snackBarErrorMessage.collect { error ->
                val result = snackBarHostState.showSnackbar(
                    message = when (error) {
                        is Error.ErrorLoadingData -> errorLoadingDataMessage
                        is Error.ErrorRateLimit -> errorRateLimitMessage },
                    actionLabel = retryLabel,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    marketViewModel.refreshCryptocurrenciesInMarket()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        LazyColumn {
            if (isLoading) {
                items(10) {
                    CryptocurrencyMarketItemSkeleton()
                }
            } else {
                items(cryptocurrencies) { itemCryptocurrency ->
                    CryptocurrencyMarketItem(
                        itemCryptocurrencyMarket = itemCryptocurrency,
                        onClickToItem = {
                            openChart.invoke(
                                ItemCryptocurrencyInChartScreen(
                                    id = itemCryptocurrency.id,
                                    symbol = itemCryptocurrency.symbol,
                                    name = itemCryptocurrency.name,
                                    current_price = itemCryptocurrency.current_price
                                ),
                                0.0
                            )
                        },
                        onClickToBuy = {
                            marketViewModel.onClickToBuy(itemCryptocurrency)
                        }
                    )
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
            balance = marketViewModel.getAvailableBalance(),
            onDismiss = {
                marketViewModel.onDialogDismiss()
            },
            onBuy = { amount, cost ->
                marketViewModel.buyCryptocurrency(cryptocurrency, amount, cost) {
                    openWallet.invoke()
                }
                marketViewModel.onDialogDismiss()
            }
        )
    }
}

@Composable
fun CryptocurrencyMarketItem(
    itemCryptocurrencyMarket: ItemCryptocurrencyInMarket,
    onClickToItem: () -> Unit,
    onClickToBuy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClickToItem.invoke() }
    ) {
        AsyncImage(
            model = itemCryptocurrencyMarket.image,
            contentDescription = itemCryptocurrencyMarket.name,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = itemCryptocurrencyMarket.name,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = itemCryptocurrencyMarket.symbol.uppercase(),
                    fontSize = 18.sp,
                    modifier = Modifier.alpha(0.5f)
                )
            }
            Text(
                text = formatCurrency(itemCryptocurrencyMarket.current_price),
                fontSize = 14.sp
            )
        }
        Button(
            onClick = onClickToBuy,
            modifier = Modifier.align(Alignment.CenterVertically))
        {
            Text(
                text = stringResource(R.string.buy_title),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun CryptocurrencyMarketItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterVertically)
                .shimmerBackground(RoundedCornerShape(4.dp))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(128.dp)
                    .height(18.dp)
                    .shimmerBackground()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(14.dp)
                    .shimmerBackground()
            )
        }
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(24.dp)
                .shimmerBackground(RoundedCornerShape(4.dp))
                .align(Alignment.CenterVertically)
        )
    }
}