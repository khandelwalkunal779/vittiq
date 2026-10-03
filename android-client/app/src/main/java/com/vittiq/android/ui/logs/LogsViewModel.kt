package com.vittiq.android.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.repository.VittiqRepository
import com.vittiq.android.ui.components.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class LogsUiState(
    val monthYearText: String = "",
    val groupedTransactions: Map<String, List<Transaction>> = emptyMap(),
    val accounts: List<Account> = emptyList(),
    val currencyRates: List<CurrencyRate> = emptyList(),
    val totalTransactionsCount: Int = 0
)

class LogsViewModel(
    private val repository: VittiqRepository
) : ViewModel() {

    // Default to July 2026 to showcase reference mockups immediately
    private val activeCalendar = MutableStateFlow<Calendar>(
        Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.JULY)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    )

    val uiState: StateFlow<LogsUiState> = combine(
        activeCalendar,
        repository.getAllTransactions(),
        repository.getAllAccounts(),
        repository.getAllCurrencyRates()
    ) { cal, allTxs, accounts, rates ->
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYearText = monthYearFormat.format(cal.time)

        val targetYear = cal.get(Calendar.YEAR)
        val targetMonth = cal.get(Calendar.MONTH)

        val txCal = Calendar.getInstance()
        val filteredTxs = allTxs.filter { tx ->
            txCal.timeInMillis = tx.timestamp
            txCal.get(Calendar.YEAR) == targetYear && txCal.get(Calendar.MONTH) == targetMonth
        }

        // Group by formatted date header (e.g., "Today · July 9", "Yesterday · July 8", etc.)
        val grouped = filteredTxs
            .groupBy { tx -> Formatters.formatGroupDateHeader(tx.timestamp) }

        LogsUiState(
            monthYearText = monthYearText,
            groupedTransactions = grouped,
            accounts = accounts,
            currencyRates = rates,
            totalTransactionsCount = filteredTxs.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LogsUiState()
    )

    fun previousMonth() {
        val newCal = (activeCalendar.value.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        activeCalendar.value = newCal
    }

    fun nextMonth() {
        val newCal = (activeCalendar.value.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        activeCalendar.value = newCal
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
        }
    }

    class Factory(private val repository: VittiqRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LogsViewModel(repository) as T
        }
    }
}
