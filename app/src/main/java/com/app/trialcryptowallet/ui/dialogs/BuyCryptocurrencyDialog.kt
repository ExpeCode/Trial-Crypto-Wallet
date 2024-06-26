package com.app.trialcryptowallet.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.app.trialcryptowallet.R
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInBuyCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInMarket
import com.app.trialcryptowallet.utils.formatCurrency
import com.app.trialcryptowallet.utils.formatToEightDecimals

@Preview(showBackground = true)
@Composable
fun BuyCryptocurrencyDialog(
    itemCryptocurrencyInBuyCryptocurrencyDialog: ItemCryptocurrencyInBuyCryptocurrencyDialog = ItemCryptocurrencyInBuyCryptocurrencyDialog("btc","Bitcoin",64000.0),
    balance: Double = 0.0,
    onDismiss: () -> Unit = {},
    onBuy: (amount: Double, cost: Double) -> Unit = { _, _ -> }
) {
    var amount by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var lastFieldChanged by remember { mutableStateOf("") }

    val amountDouble = amount.toDoubleOrNull() ?: 0.0
    val costDouble = cost.toDoubleOrNull() ?: 0.0

    val isBalanceEnoughForCost = balance >= costDouble
    val isBalanceEnoughForAmount = balance >= amountDouble * itemCryptocurrencyInBuyCryptocurrencyDialog.current_price

    if (lastFieldChanged == "amount") {
        cost = formatToEightDecimals(amountDouble * itemCryptocurrencyInBuyCryptocurrencyDialog.current_price)
    } else if (lastFieldChanged == "cost") {
        amount = formatToEightDecimals(costDouble / itemCryptocurrencyInBuyCryptocurrencyDialog.current_price)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.buy, itemCryptocurrencyInBuyCryptocurrencyDialog.name)) },
        text = {
            Column {
                Text(stringResource(R.string.available_balance, formatCurrency(balance)))
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        lastFieldChanged = "amount"
                    },
                    label = { Text(stringResource(R.string.amount_title)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = cost,
                    onValueChange = {
                        cost = it
                        lastFieldChanged = "cost"
                    },
                    label = { Text(stringResource(R.string.cost)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (!isBalanceEnoughForCost || !isBalanceEnoughForAmount) {
                    Text(
                        text = stringResource(R.string.insufficient_funds),
                        color = Color.Red
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isBalanceEnoughForAmount) onBuy(amountDouble, amountDouble * itemCryptocurrencyInBuyCryptocurrencyDialog.current_price)
                    else if (isBalanceEnoughForCost) onBuy(costDouble / itemCryptocurrencyInBuyCryptocurrencyDialog.current_price, costDouble)
                },
                enabled = isBalanceEnoughForAmount || isBalanceEnoughForCost
            ) {
                Text(stringResource(R.string.buy_title))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}