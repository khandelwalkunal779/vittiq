package com.vittiq.android.ui.components

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyAmountVisualTransformationTest {

    @Test
    fun testIndianNumberFormatting() {
        assertEquals("1", CurrencyAmountVisualTransformation.formatAmountWithCommas("1", true))
        assertEquals("12", CurrencyAmountVisualTransformation.formatAmountWithCommas("12", true))
        assertEquals("123", CurrencyAmountVisualTransformation.formatAmountWithCommas("123", true))
        assertEquals("1,234", CurrencyAmountVisualTransformation.formatAmountWithCommas("1234", true))
        assertEquals("12,345", CurrencyAmountVisualTransformation.formatAmountWithCommas("12345", true))
        assertEquals("1,23,456", CurrencyAmountVisualTransformation.formatAmountWithCommas("123456", true))
        assertEquals("12,34,567", CurrencyAmountVisualTransformation.formatAmountWithCommas("1234567", true))
        assertEquals("1,23,45,678", CurrencyAmountVisualTransformation.formatAmountWithCommas("12345678", true))
        assertEquals("12,34,567.89", CurrencyAmountVisualTransformation.formatAmountWithCommas("1234567.89", true))
        assertEquals("12,34,567.", CurrencyAmountVisualTransformation.formatAmountWithCommas("1234567.", true))
        assertEquals(".5", CurrencyAmountVisualTransformation.formatAmountWithCommas(".5", true))
        assertEquals("", CurrencyAmountVisualTransformation.formatAmountWithCommas("", true))
    }

    @Test
    fun testInternationalNumberFormatting() {
        assertEquals("1", CurrencyAmountVisualTransformation.formatAmountWithCommas("1", false))
        assertEquals("123", CurrencyAmountVisualTransformation.formatAmountWithCommas("123", false))
        assertEquals("1,234", CurrencyAmountVisualTransformation.formatAmountWithCommas("1234", false))
        assertEquals("12,345", CurrencyAmountVisualTransformation.formatAmountWithCommas("12345", false))
        assertEquals("123,456", CurrencyAmountVisualTransformation.formatAmountWithCommas("123456", false))
        assertEquals("1,234,567", CurrencyAmountVisualTransformation.formatAmountWithCommas("1234567", false))
        assertEquals("1,234,567.89", CurrencyAmountVisualTransformation.formatAmountWithCommas("1234567.89", false))
    }

    @Test
    fun testOffsetMappingBidirectional() {
        val transformation = CurrencyAmountVisualTransformation(isIndianFormat = true)
        val raw = "1234567.89"
        val result = transformation.filter(AnnotatedString(raw))

        assertEquals("12,34,567.89", result.text.text)

        val mapping = result.offsetMapping
        for (i in 0..raw.length) {
            val transOffset = mapping.originalToTransformed(i)
            assert(transOffset in 0..result.text.text.length) { "Out of bounds at original offset $i: $transOffset" }
        }

        for (j in 0..result.text.text.length) {
            val origOffset = mapping.transformedToOriginal(j)
            assert(origOffset in 0..raw.length) { "Out of bounds at transformed offset $j: $origOffset" }
        }

        assertEquals(result.text.text.length, mapping.originalToTransformed(raw.length))
        assertEquals(raw.length, mapping.transformedToOriginal(result.text.text.length))

        assertEquals(0, mapping.originalToTransformed(0))
        assertEquals(0, mapping.transformedToOriginal(0))
    }
}
