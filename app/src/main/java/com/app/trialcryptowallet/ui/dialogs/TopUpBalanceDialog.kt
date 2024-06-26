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
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInSellCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInWallet
import com.app.trialcryptowallet.utils.formatCurrency

@Preview(showBackground = true)
@Composable
fun TopUpBalanceDialog(
    onDismiss: () -> Unit = {},
    onAddBalance: (amount: Double) -> Unit = {}
) {
    var amount by remember { mutableStateOf("") }
    val amountDouble = amount.toDoubleOrNull() ?: 0.0
    val isAmountValid = amountDouble > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.top_up_balance)) },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.amount_title)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                if (!isAmountValid) {
                    Text(
                        text = stringResource(R.string.please_enter_correct_amount),
                        color = Color.Red
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (isAmountValid) onAddBalance(amountDouble) },
                enabled = isAmountValid
            ) {
                Text(stringResource(R.string.top_up))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}