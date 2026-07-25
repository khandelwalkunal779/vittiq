package com.example.vittiq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vittiq.VittiqApplication
import com.example.vittiq.data.db.Account
import com.example.vittiq.data.db.Transaction
import com.example.vittiq.data.db.TransactionType
import com.example.vittiq.data.repository.VittiqRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class LogsUiState(
    val transactionsByDate: Map<String, List<Transaction>> = emptyMap(),
    val allAccounts: List<Account> = emptyList(),
    val isLoading: Boolean = true
)

class LogsViewModel(private val repository: VittiqRepository) : ViewModel() {

    val availableCategories = listOf(
        "Food & Dining", "Shopping", "Housing", "Transportation",
        "Entertainment", "Income", "Investments", "Transfers",
        "Health", "Utilities", "Other"
    )

    val uiState: StateFlow<LogsUiState> = combine(
        repository.allTransactionsFlow,
        repository.allAccountsFlow
    ) { transactions, accounts ->
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val grouped = transactions.groupBy { tx ->
            dateFormat.format(Date(tx.dateTime))
        }
        LogsUiState(
            transactionsByDate = grouped,
            allAccounts = accounts,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LogsUiState()
    )

    fun addTransaction(
        name: String,
        amount: Double,
        type: TransactionType,
        category: String,
        accountId: String,
        dateTime: Long,
        description: String
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                dateTime = dateTime,
                accountId = accountId,
                category = category,
                amount = amount,
                name = name,
                description = description,
                type = type
            )
            repository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VittiqApplication
                LogsViewModel(application.container.repository)
            }
        }
    }
}
