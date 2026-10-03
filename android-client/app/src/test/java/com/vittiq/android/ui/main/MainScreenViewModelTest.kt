package com.vittiq.android.ui.main

import com.vittiq.android.ui.components.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

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
}
