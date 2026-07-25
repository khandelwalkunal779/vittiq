package com.example.vittiq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vittiq.VittiqApplication
import com.example.vittiq.data.db.Account
import com.example.vittiq.data.db.AccountType
import com.example.vittiq.data.repository.VittiqRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class OverallsUiState(
    val cashAccounts: List<Account> = emptyList(),
    val bankAccounts: List<Account> = emptyList(),
    val cardAccounts: List<Account> = emptyList(),
    val investmentAccounts: List<Account> = emptyList(),
    val cashTotal: Double = 0.0,
    val bankTotal: Double = 0.0,
    val cardTotal: Double = 0.0,
    val investmentTotal: Double = 0.0,
    val netWorth: Double = 0.0,
    val isLoading: Boolean = true
)

class OverallsViewModel(private val repository: VittiqRepository) : ViewModel() {

    val uiState: StateFlow<OverallsUiState> = repository.allAccountsFlow.map { accounts ->
        val cash = accounts.filter { it.type == AccountType.CASH }
        val bank = accounts.filter { it.type == AccountType.BANK_ACCOUNT }
        val card = accounts.filter { it.type == AccountType.CARD }
        val investment = accounts.filter { it.type == AccountType.INVESTMENT }

        val cashSum = cash.sumOf { it.currentBalance }
        val bankSum = bank.sumOf { it.currentBalance }
        val cardSum = card.sumOf { it.currentBalance }
        val investmentSum = investment.sumOf { it.currentBalance }

        val totalNet = cashSum + bankSum + cardSum + investmentSum

        OverallsUiState(
            cashAccounts = cash,
            bankAccounts = bank,
            cardAccounts = card,
            investmentAccounts = investment,
            cashTotal = cashSum,
            bankTotal = bankSum,
            cardTotal = cardSum,
            investmentTotal = investmentSum,
            netWorth = totalNet,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OverallsUiState()
    )

    fun addAccount(name: String, type: AccountType, initialBalance: Double) {
        viewModelScope.launch {
            val account = Account(
                id = UUID.randomUUID().toString(),
                name = name,
                type = type,
                initialBalance = initialBalance,
                currentBalance = initialBalance
            )
            repository.insertAccount(account)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VittiqApplication
                OverallsViewModel(application.container.repository)
            }
        }
    }
}
