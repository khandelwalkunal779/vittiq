package com.vittiq.android.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleAiCoreCategoryClassifierTest {

    private val categories = listOf(
        "Food & Dining",
        "Groceries",
        "Income",
        "Transportation",
        "Housing",
        "Transfer",
        "Other"
    )

    @Test
    fun testPromptContainsStrictTransferRule() {
        val classifier = GoogleAiCoreCategoryClassifier()
        val prompt = classifier.buildPrompt("Bank to Cash", categories)

        assertTrue(prompt.contains("Bank to Cash"))
        assertTrue(prompt.contains("Food & Dining, Groceries, Income, Transportation, Housing, Transfer, Other"))
        assertTrue(prompt.contains("transfers and balances must strictly be categorised under 'Transfer'"))
    }

    @Test
    fun testMatchCategoryExactAndSubstring() {
        val classifier = GoogleAiCoreCategoryClassifier()

        assertEquals("Food & Dining", classifier.matchCategory("Food & Dining", categories))
        assertEquals("Transfer", classifier.matchCategory("transfer", categories))
        assertEquals("Groceries", classifier.matchCategory("\"Groceries\"", categories))
        assertEquals("Transportation", classifier.matchCategory("Category: Transportation", categories))
        assertNull(classifier.matchCategory("Unrelated Category", categories))
    }
}
