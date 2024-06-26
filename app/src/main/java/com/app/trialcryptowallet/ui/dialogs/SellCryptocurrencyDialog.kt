package com.app.trialcryptowallet.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.trialcryptowallet.R
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInBuyCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInMarket
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInSellCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInWallet
import com.app.trialcryptowallet.utils.formatCurrency

@Preview(showBackground = true)
@Composable
fun SellCryptocurrencyDialog(
    itemCryptocurrencyInSellCryptocurrencyDialog: ItemCryptocurrencyInSellCryptocurrencyDialog = ItemCryptocurrencyInSellCryptocurrencyDialog("btc","BTC","Bitcoin",64000.0, 0.55),
    onDismiss: () -> Unit = {},
    onSell: (amount: Double, price: Double) -> Unit = { _, _ -> }
) {
    var amount by remember { mutableStateOf("") }
    val amountDouble = amount.toDoubleOrNull() ?: 0.0
    val price = amountDouble * itemCryptocurrencyInSellCryptocurrencyDialog.current_price
    val isAmountValid = itemCryptocurrencyInSellCryptocurrencyDialog.available_amount >= amountDouble

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sell, itemCryptocurrencyInSellCryptocurrencyDialog.name)) },
        text = {
            Column {
                Text(stringResource(R.string.available,"${itemCryptocurrencyInSellCryptocurrencyDialog.available_amount} ${itemCryptocurrencyInSellCryptocurrencyDialog.symbol.uppercase()}"))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.amount_title)) }
                )
                Text(stringResource(R.string.price, formatCurrency(price)))
                if (!isAmountValid) {
                    Text(
                        text = stringResource(R.string.not_enough_cryptocurrency),
                        color = Color.Red
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (isAmountValid) onSell(amountDouble, price) },
                enabled = isAmountValid
            ) {
                Text(stringResource(R.string.sell_title))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}