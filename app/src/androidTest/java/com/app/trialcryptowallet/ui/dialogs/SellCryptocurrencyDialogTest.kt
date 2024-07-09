package com.app.trialcryptowallet.ui.dialogs

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInSellCryptocurrencyDialog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SellCryptocurrencyDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sellCryptocurrencyDialog_DisplaysCorrectly() {
        val item = ItemCryptocurrencyInSellCryptocurrencyDialog("btc", "BTC", "Bitcoin", 64000.0, 0.55)

        composeTestRule.setContent {
            SellCryptocurrencyDialog(
                itemCryptocurrencyInSellCryptocurrencyDialog = item,
                onDismiss = {},
                onSell = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Sell Bitcoin").assertExists()

        composeTestRule.onNodeWithText("Amount").assertExists()

        composeTestRule.onNodeWithText("Sell").performClick()
    }
}