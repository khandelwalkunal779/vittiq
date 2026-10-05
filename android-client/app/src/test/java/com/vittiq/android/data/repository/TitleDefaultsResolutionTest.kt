package com.vittiq.android.data.repository

import com.vittiq.android.data.model.TitleDefaults
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class TitleDefaultsResolutionTest {

    private fun resolveFromList(title: String, txs: List<Transaction>): TitleDefaults? {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return null
        val matching = txs.filter { it.name.trim().equals(trimmed, ignoreCase = true) }
        if (matching.isEmpty()) return null

        val dominantType = matching.groupingBy { it.type }.eachCount().maxByOrNull { it.value }?.key ?: TransactionType.DEBIT
        val typeFiltered = matching.filter { it.type == dominantType }

        return if (dominantType == TransactionType.TRANSFER) {
            val mostFrequentPair = typeFiltered
                .groupingBy { Pair(it.accountId, it.toAccountId) }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key

            if (mostFrequentPair != null && mostFrequentPair.second != null) {
                TitleDefaults(
                    title = trimmed,
                    type = TransactionType.TRANSFER,
                    primaryAccountId = mostFrequentPair.first,
                    toAccountId = mostFrequentPair.second
                )
            } else {
                null
            }
        } else {
            val mostFrequentAccount = typeFiltered
                .groupingBy { it.accountId }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key

            if (mostFrequentAccount != null) {
                TitleDefaults(
                    title = trimmed,
                    type = dominantType,
                    primaryAccountId = mostFrequentAccount,
                    toAccountId = null
                )
            } else {
                null
            }
        }
    }

    @Test
    fun testFrequentAccountSelectionForDebit() {
        val txs = listOf(
            Transaction(id = "1", timestamp = 1000L, accountId = "acc-hdfc", name = "Chipotle", category = "Food", amount = 250.0, type = TransactionType.DEBIT),
            Transaction(id = "2", timestamp = 2000L, accountId = "acc-cash", name = "Chipotle", category = "Food", amount = 200.0, type = TransactionType.DEBIT),
            Transaction(id = "3", timestamp = 3000L, accountId = "acc-hdfc", name = "Chipotle", category = "Food", amount = 300.0, type = TransactionType.DEBIT)
        )

        val result = resolveFromList("chipotle", txs)
        assertNotNull(result)
        assertEquals("acc-hdfc", result?.primaryAccountId)
        assertEquals(TransactionType.DEBIT, result?.type)
        assertNull(result?.toAccountId)
    }

    @Test
    fun testFrequentTransferPairSelection() {
        val txs = listOf(
            Transaction(id = "1", timestamp = 1000L, accountId = "acc-bank", toAccountId = "acc-cash", name = "Bank to Cash", category = "Transfer", amount = 5000.0, type = TransactionType.TRANSFER),
            Transaction(id = "2", timestamp = 2000L, accountId = "acc-bank", toAccountId = "acc-wallet", name = "Bank to Cash", category = "Transfer", amount = 1000.0, type = TransactionType.TRANSFER),
            Transaction(id = "3", timestamp = 3000L, accountId = "acc-bank", toAccountId = "acc-cash", name = "Bank to Cash", category = "Transfer", amount = 2000.0, type = TransactionType.TRANSFER)
        )

        val result = resolveFromList("Bank to Cash", txs)
        assertNotNull(result)
        assertEquals(TransactionType.TRANSFER, result?.type)
        assertEquals("acc-bank", result?.primaryAccountId)
        assertEquals("acc-cash", result?.toAccountId)
    }

    @Test
    fun testEmptyOrNonMatchingReturnsNull() {
        val txs = listOf(
            Transaction(id = "1", timestamp = 1000L, accountId = "acc-bank", name = "Grocery", category = "Food", amount = 500.0, type = TransactionType.DEBIT)
        )

        assertNull(resolveFromList("", txs))
        assertNull(resolveFromList("Unknown Place", txs))
    }
}
