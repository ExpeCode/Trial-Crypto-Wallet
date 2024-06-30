package com.app.trialcryptowallet.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.app.trialcryptowallet.R
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInChartScreen
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInWallet
import com.app.trialcryptowallet.ui.dialogs.BuyCryptocurrencyDialog
import com.app.trialcryptowallet.ui.dialogs.SellCryptocurrencyDialog
import com.app.trialcryptowallet.ui.dialogs.TopUpBalanceDialog
import com.app.trialcryptowallet.ui.dialogs.WithdrawFundsDialog
import com.app.trialcryptowallet.utils.formatCurrency
import com.app.trialcryptowallet.utils.shimmerBackground
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
@Composable
fun WalletScreen(
    walletViewModel: WalletViewModel = koinViewModel(),
    openChart: (ItemCryptocurrencyInChartScreen, Double) -> Unit = { _, _ -> },
    openMarket: () -> Unit = {},
) {

    val cryptocurrenciesInWallet by walletViewModel.cryptocurrenciesInWallet.collectAsState()
    val availableBalance by walletViewModel.availableBalance.collectAsState()
    val balanceCrypto by walletViewModel.balanceCrypto.collectAsState()
    val itemCryptocurrencyInBuyCryptocurrencyDialog by walletViewModel.itemCryptocurrencyInBuyCryptocurrencyDialog.collectAsState()
    val itemCryptocurrencyInSellCryptocurrencyDialog by walletViewModel.itemCryptocurrencyInSellCryptocurrencyDialog.collectAsState()
    val isLoading by walletViewModel.isLoading.collectAsState()
    val showTopUpBalanceDialog by walletViewModel.showTopUpBalanceDialog.collectAsState()
    val showWithdrawFundsDialog by walletViewModel.showWithdrawFundsDialog.collectAsState()
    /*val availableBalance by remember { mutableDoubleStateOf(900.0) }
    val balanceCrypto by remember { mutableDoubleStateOf(100.0) }
    val cryptocurrenciesInWallet = remember {
        mutableListOf<ItemCryptocurrencyInWallet>().apply {
            add(ItemCryptocurrencyInWallet("eth", "ETH", "Etherium", "", 12999.4, 0.43))
            add(ItemCryptocurrencyInWallet("btc", "BTC", "Bitcoin", "", 64000.4, 0.25))
        }
    }
    val itemCryptocurrencyInBuyCryptocurrencyDialog by remember {
        mutableStateOf<ItemCryptocurrencyInBuyCryptocurrencyDialog?>(null)
        //mutableStateOf(ItemCryptocurrencyInBuyCryptocurrencyDialog("eth","Etherium",12999.4))
    }
    val itemCryptocurrencyInSellCryptocurrencyDialog by remember {
        mutableStateOf<ItemCryptocurrencyInSellCryptocurrencyDialog?>(null)
        //mutableStateOf(ItemCryptocurrencyInSellCryptocurrencyDialog("eth","ETH","Etherium",12999.4, 0.26))
    }
    val isLoading by remember {
        mutableStateOf(false)
    }
    val showTopUpBalanceDialog by remember {
        mutableStateOf(false)
    }
    val showWithdrawFundsDialog by remember {
        mutableStateOf(false)
    }*/

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            walletViewModel.refreshCryptocurrenciesInWallet()
        }
    }

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = stringResource(R.string.total_balance),
            color = MaterialTheme.colorScheme.inversePrimary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp))
        Text(
            text = formatCurrency(availableBalance + balanceCrypto),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp))
        Text(
            text = stringResource(R.string.available_balance, formatCurrency(availableBalance)),
            color = MaterialTheme.colorScheme.inversePrimary,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .align(Alignment.End))
        Text(
            text = stringResource(R.string.total_value_of_cryptocurrencies, formatCurrency(balanceCrypto)),
            color = MaterialTheme.colorScheme.inversePrimary,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.End))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly)
        {
            ElevatedButton(
                onClick = {
                    walletViewModel.showTopUpBalanceDialog()
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(text = stringResource(R.string.top_up_balance))
            }
            Button(
                onClick = {
                    walletViewModel.showWithdrawFundsDialog()
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(text = stringResource(R.string.withdraw_funds))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(16.dp)
        ) {
            if (isLoading) {
                LazyColumn {
                    items(6) {
                        CryptocurrencyItemSkeleton()
                    }
                }
            } else if (cryptocurrenciesInWallet.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = stringResource(R.string.empty_wallet_message),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center)
                    Button(
                        onClick = openMarket,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = stringResource(R.string.go_to))
                    }
                }
            } else {
                LazyColumn {
                    items(cryptocurrenciesInWallet) { itemCryptocurrencyInWallet ->
                        val sellAction = SwipeAction(
                            onSwipe = {
                                walletViewModel.onClickToSell(itemCryptocurrencyInWallet)
                            },
                            icon = {
                                Text(
                                    text = stringResource(R.string.sell_title),
                                    modifier = Modifier.padding(all = 16.dp)
                                )
                            },
                            background = Color.Red.copy(alpha = 0.7f)
                        )
                        val buyAction = SwipeAction(
                            onSwipe = {
                                walletViewModel.onClickToBuy(itemCryptocurrencyInWallet)
                            },
                            icon = {
                                Text(
                                    text = stringResource(R.string.buy_title),
                                    modifier = Modifier.padding(all = 16.dp)
                                )
                            },
                            background = Color(0xFF50B384).copy(alpha = 0.7f)
                        )
                        SwipeableActionsBox(
                            modifier = Modifier,
                            swipeThreshold = 200.dp,
                            startActions = listOf(sellAction),
                            endActions = listOf(buyAction)
                        ) {
                            CryptocurrencyItem(itemCryptocurrencyInWallet) {
                                openChart.invoke(
                                    ItemCryptocurrencyInChartScreen(
                                        id = itemCryptocurrencyInWallet.id,
                                        symbol = itemCryptocurrencyInWallet.symbol,
                                        name = itemCryptocurrencyInWallet.name,
                                        current_price = itemCryptocurrencyInWallet.current_price
                                    ),
                                    itemCryptocurrencyInWallet.amount
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTopUpBalanceDialog) {
        TopUpBalanceDialog(
            onDismiss = { walletViewModel.onDismissTopUpBalanceDialog() },
            onAddBalance = { amount ->
                walletViewModel.topUpBalance(amount)
                walletViewModel.onDismissTopUpBalanceDialog()
            }
        )
    }

    if (showWithdrawFundsDialog) {
        WithdrawFundsDialog(
            currentBalance = availableBalance,
            onDismiss = { walletViewModel.onDismissWithdrawFundsDialog() },
            onWithdrawBalance = { amount ->
                walletViewModel.withdrawFunds(amount)
                walletViewModel.onDismissWithdrawFundsDialog()
            }
        )
    }

    itemCryptocurrencyInBuyCryptocurrencyDialog?.let { cryptocurrency ->
        BuyCryptocurrencyDialog(
            itemCryptocurrencyInBuyCryptocurrencyDialog = cryptocurrency,
            balance = availableBalance,
            onDismiss = {
                walletViewModel.onDismissBuyCryptocurrencyDialog()
            },
            onBuy = { amount, cost ->
                walletViewModel.buyCryptocurrency(cryptocurrency, amount, cost)
                walletViewModel.onDismissBuyCryptocurrencyDialog()
            }
        )
    }

    itemCryptocurrencyInSellCryptocurrencyDialog?.let { cryptocurrency ->
        SellCryptocurrencyDialog(
            itemCryptocurrencyInSellCryptocurrencyDialog = cryptocurrency,
            onDismiss = {
                walletViewModel.onDismissSellCryptocurrencyDialog()
            },
            onSell = { amount, price ->
                walletViewModel.sellCryptocurrency(cryptocurrency, amount, price)
                walletViewModel.onDismissSellCryptocurrencyDialog()
            }
        )
    }
}

@Composable
fun CryptocurrencyItem(
    itemCryptocurrencyInWallet: ItemCryptocurrencyInWallet,
    onClickToItem: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClickToItem.invoke() }
    ) {
        AsyncImage(
            model = itemCryptocurrencyInWallet.image,
            contentDescription = itemCryptocurrencyInWallet.name,
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
                    text = itemCryptocurrencyInWallet.name,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = itemCryptocurrencyInWallet.symbol.uppercase(),
                    fontSize = 18.sp,
                    modifier = Modifier.alpha(0.5f)
                )
            }
            Text(
                text = stringResource(R.string.price, formatCurrency(itemCryptocurrencyInWallet.current_price)),
                fontSize = 14.sp
            )
            Text(
                text = stringResource(R.string.amount, itemCryptocurrencyInWallet.amount),
                fontSize = 14.sp
            )
        }
        Text(
            text = formatCurrency(itemCryptocurrencyInWallet.current_price * itemCryptocurrencyInWallet.amount),
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun CryptocurrencyItemSkeleton() {
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
                .width(40.dp)
                .height(18.dp)
                .shimmerBackground(RoundedCornerShape(4.dp))
                .align(Alignment.CenterVertically)
        )
    }
}