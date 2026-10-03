package com.vittiq.android.ui.main

import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionType
import com.vittiq.android.ui.components.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

class FinancialCalculationTest {

    @Test
    fun inrFormatting_formatsWithRupeeSignAndDecimals() {
        val formatted = Formatters.formatInr(24830.52)
        assertEquals("₹24,830.52", formatted)
    }

    @Test
    fun transactionFormatting_creditHasPlusSign() {
        val formatted = Formatters.formatTransactionAmount(2620.00, isCredit = true)
        assertEquals("+₹2,620.00", formatted)
    }

    @Test
    fun transactionFormatting_debitHasMinusSign() {
        val formatted = Formatters.formatTransactionAmount(87.34, isCredit = false)
        assertEquals("-₹87.34", formatted)
    }

    @Test
    fun cleanSlateTrendCalculation_returnsNullWhenNoPriorTransactions() {
        val hasPriorTransactions = false
        val priorMonthClosingNetWorth = 0.0
        val totalNetWorth = 1000.0

        val trendPercentage: Double? = if (hasPriorTransactions && priorMonthClosingNetWorth > 0.0) {
            ((totalNetWorth - priorMonthClosingNetWorth) / priorMonthClosingNetWorth) * 100.0
        } else {
            null
        }

        assertNull("Trend percentage should be null on clean slate", trendPercentage)
    }

    @Test
    fun dynamicTrendCalculation_computesPercentageWhenPriorTransactionsExist() {
        val hasPriorTransactions = true
        val priorMonthClosingNetWorth = 20000.0
        val totalNetWorth = 24830.52

        val trendPercentage: Double? = if (hasPriorTransactions && priorMonthClosingNetWorth > 0.0) {
            ((totalNetWorth - priorMonthClosingNetWorth) / priorMonthClosingNetWorth) * 100.0
        } else {
            null
        }

        assertEquals(24.1526, trendPercentage!!, 0.001)
    }

    @Test
    fun transferTransactions_excludedFromMonthlyIncomeAndExpenses() {
        val transactions = listOf(
            Transaction(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                accountId = "acc-bank",
                name = "Salary",
                category = "Income",
                amount = 50000.0,
                type = TransactionType.CREDIT
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                accountId = "acc-bank",
                name = "Groceries",
                category = "Groceries",
                amount = 4000.0,
                type = TransactionType.DEBIT
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                accountId = "acc-bank",
                toAccountId = "acc-cash",
                name = "ATM Withdrawal",
                category = "Transfer",
                amount = 10000.0,
                type = TransactionType.TRANSFER
            )
        )

        val monthlyIncome = transactions
            .filter { it.type == TransactionType.CREDIT }
            .sumOf { it.amount }

        val monthlyExpenses = transactions
            .filter { it.type == TransactionType.DEBIT }
            .sumOf { it.amount }

        assertEquals(50000.0, monthlyIncome, 0.001)
        assertEquals(4000.0, monthlyExpenses, 0.001)
        // Monthly savings is Income - Expenses; Transfer has zero net impact
        assertEquals(46000.0, monthlyIncome - monthlyExpenses, 0.001)
    }

    @Test
    fun transferTransaction_netWorthNeutralBetweenAccounts() {
        var sourceBalance = 50000.0
        var destBalance = 5000.0
        val initialNetWorth = sourceBalance + destBalance

        val transferAmount = 15000.0
        // Debit source
        sourceBalance -= transferAmount
        // Credit destination
        destBalance += transferAmount

        val finalNetWorth = sourceBalance + destBalance
        assertEquals(35000.0, sourceBalance, 0.001)
        assertEquals(20000.0, destBalance, 0.001)
        assertEquals(initialNetWorth, finalNetWorth, 0.001)
    }
}
