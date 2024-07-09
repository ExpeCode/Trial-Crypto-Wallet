package com.app.trialcryptowallet.ui.dialogs

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.app.trialcryptowallet.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopUpBalanceDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun topUpBalanceDialog_DisplaysCorrectly() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            TopUpBalanceDialog(
                onDismiss = {},
                onAddBalance = { }
            )
        }

        val topUpBalanceText = context.getString(R.string.top_up_balance)
        composeTestRule.onNodeWithText(topUpBalanceText).assertExists()

        val amountText = context.getString(R.string.amount_title)
        composeTestRule.onNodeWithText(amountText).assertExists()

        val topUpText = context.getString(R.string.top_up)
        composeTestRule.onNodeWithText(topUpText).performClick()
    }
}