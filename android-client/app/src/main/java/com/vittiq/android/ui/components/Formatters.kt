package com.vittiq.android.ui.components

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {
    private val inrDecimalFormat = DecimalFormat("#,##,##0.00")
    private val inrIntegerFormat = DecimalFormat("#,##,##0")

    fun formatInr(amount: Double, includeDecimals: Boolean = true): String {
        val formatted = if (includeDecimals) {
            inrDecimalFormat.format(amount)
        } else {
            inrIntegerFormat.format(amount)
        }
        return "₹$formatted"
    }

    fun formatTransactionAmount(amount: Double, isCredit: Boolean): String {
        val sign = if (isCredit) "+" else "-"
        return "$sign${formatInr(amount, includeDecimals = true)}"
    }

    fun splitInr(amount: Double): Pair<String, String> {
        val formatted = inrDecimalFormat.format(amount)
        val parts = formatted.split(".")
        val integerPart = parts.getOrNull(0) ?: "0"
        val fractionalPart = if (parts.size > 1) ".${parts[1]}" else ".00"
        return Pair(integerPart, fractionalPart)
    }

    fun formatMonthYear(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatGroupDateHeader(timestamp: Long): String {
        val now = Calendar.getInstance()
        val txDate = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isSameYear = now.get(Calendar.YEAR) == txDate.get(Calendar.YEAR)
        val isToday = isSameYear && now.get(Calendar.DAY_OF_YEAR) == txDate.get(Calendar.DAY_OF_YEAR)

        val yesterday = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = isSameYear && yesterday.get(Calendar.DAY_OF_YEAR) == txDate.get(Calendar.DAY_OF_YEAR)

        val monthDayFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
        val dayOfWeekFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        return when {
            isToday -> "Today · ${monthDayFormat.format(Date(timestamp))}"
            isYesterday -> "Yesterday · ${monthDayFormat.format(Date(timestamp))}"
            else -> dayOfWeekFormat.format(Date(timestamp))
        }
    }
}
