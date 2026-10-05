package com.vittiq.android.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * VisualTransformation that formats numbers with comma separators:
 * - Indian numbering system (#,##,##0.##) when [isIndianFormat] is true
 * - International standard (#,###.##) when [isIndianFormat] is false
 *
 * Maintains a robust two-way OffsetMapping to prevent cursor jumps and IndexOutOfBoundsException.
 */
class CurrencyAmountVisualTransformation(
    val isIndianFormat: Boolean = true
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formatted = formatAmountWithCommas(raw, isIndianFormat)
        val offsetMapping = createOffsetMapping(raw, formatted)

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = offsetMapping
        )
    }

    companion object {
        fun formatAmountWithCommas(text: String, isIndianFormat: Boolean): String {
            if (text.isEmpty()) return ""
            val parts = text.split('.', limit = 2)
            val intPart = parts[0]
            val hasDecimal = parts.size > 1 || text.contains('.')
            val decPart = if (parts.size > 1) parts[1] else ""

            val formattedInt = if (isIndianFormat) {
                formatIndianInteger(intPart)
            } else {
                formatInternationalInteger(intPart)
            }

            return if (hasDecimal) "$formattedInt.$decPart" else formattedInt
        }

        private fun formatIndianInteger(raw: String): String {
            if (raw.length <= 3) return raw
            val last3 = raw.takeLast(3)
            val remaining = raw.dropLast(3)

            val sb = StringBuilder()
            val firstChunkLen = if (remaining.length % 2 == 0) 2 else 1
            sb.append(remaining.take(firstChunkLen))
            var i = firstChunkLen
            while (i < remaining.length) {
                sb.append(',')
                sb.append(remaining.substring(i, i + 2))
                i += 2
            }
            sb.append(',')
            sb.append(last3)
            return sb.toString()
        }

        private fun formatInternationalInteger(raw: String): String {
            if (raw.length <= 3) return raw
            val sb = StringBuilder()
            val firstChunkLen = if (raw.length % 3 == 0) 3 else raw.length % 3
            sb.append(raw.take(firstChunkLen))
            var i = firstChunkLen
            while (i < raw.length) {
                sb.append(',')
                sb.append(raw.substring(i, i + 3))
                i += 3
            }
            return sb.toString()
        }

        private fun createOffsetMapping(original: String, transformed: String): OffsetMapping {
            val origToTrans = IntArray(original.length + 1)
            val transToOrig = IntArray(transformed.length + 1)

            origToTrans[0] = 0
            transToOrig[0] = 0

            var oIdx = 0
            var tIdx = 0

            while (oIdx < original.length && tIdx < transformed.length) {
                if (transformed[tIdx] == ',') {
                    tIdx++
                    transToOrig[tIdx] = oIdx
                    origToTrans[oIdx] = tIdx
                } else {
                    oIdx++
                    tIdx++
                    origToTrans[oIdx] = tIdx
                    transToOrig[tIdx] = oIdx
                }
            }

            while (oIdx <= original.length) {
                origToTrans[oIdx] = transformed.length
                oIdx++
            }
            while (tIdx <= transformed.length) {
                transToOrig[tIdx] = original.length
                tIdx++
            }

            return object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val clamped = offset.coerceIn(0, original.length)
                    return origToTrans[clamped].coerceIn(0, transformed.length)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val clamped = offset.coerceIn(0, transformed.length)
                    return transToOrig[clamped].coerceIn(0, original.length)
                }
            }
        }
    }
}
