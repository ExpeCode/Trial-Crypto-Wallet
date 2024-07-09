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
class WithdrawFundsDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun withdrawFundsDialog_DisplaysCorrectly() {
        val currentBalance = 100.0 // Example balance
        val validWithdrawAmount = 50.0 // Example valid withdrawal amount
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            WithdrawFundsDialog(
                currentBalance = currentBalance,
                onDismiss = {},
                onWithdrawBalance = { }
            )
        }

        val withdrawFundsText = context.getString(R.string.withdraw_funds)
        composeTestRule.onNodeWithText(withdrawFundsText).assertExists()

        val amountText = context.getString(R.string.amount_title)
        composeTestRule.onNodeWithText(amountText).assertExists()

        composeTestRule.onNodeWithTag(TAG_OUTLINED_TEXT_FIELD).performTextInput(validWithdrawAmount.toString())

        val withdrawText = context.getString(R.string.withdraw)
        composeTestRule.onNodeWithText(withdrawText).performClick()
    }
}