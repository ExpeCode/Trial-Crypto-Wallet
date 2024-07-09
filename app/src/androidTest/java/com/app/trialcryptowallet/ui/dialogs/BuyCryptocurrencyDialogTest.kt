package com.app.trialcryptowallet.ui.dialogs

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInBuyCryptocurrencyDialog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BuyCryptocurrencyDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buyCryptocurrencyDialog_DisplaysCorrectly() {
        val item = ItemCryptocurrencyInBuyCryptocurrencyDialog("btc", "Bitcoin", 64000.0)

        composeTestRule.setContent {
            BuyCryptocurrencyDialog(
                itemCryptocurrencyInBuyCryptocurrencyDialog = item,
                balance = 1000.0,
                onDismiss = {},
                onBuy = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Buy Bitcoin").assertExists()

        composeTestRule.onNodeWithText("Amount").assertExists()
        composeTestRule.onNodeWithText("Cost").assertExists()

        composeTestRule.onNodeWithText("Buy").performClick()
    }
}
