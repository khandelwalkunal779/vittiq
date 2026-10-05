package com.vittiq.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.AccountWithCategory
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.TitleDefaults
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionType
import com.vittiq.android.data.model.UserProfile
import com.vittiq.android.data.repository.VittiqRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeCategoryItem(
    val category: AccountCategory,
    val accountsCount: Int,
    val totalBalance: Double,
    val isProfit: Boolean
)

data class HomeUiState(
    val categories: List<HomeCategoryItem> = emptyList(),
    val accountsWithCategory: List<AccountWithCategory> = emptyList(),
    val totalNetWorth: Double = 0.0,
    val monthlyTrendPercentage: Double? = null,
    val monthlyIncome: Double = 0.0,
    val monthlyExpenses: Double = 0.0,
    val monthlySavings: Double = 0.0,
    val userProfile: UserProfile? = null,
    val currencyRates: List<CurrencyRate> = emptyList()
)

class HomeViewModel(
    private val repository: VittiqRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getCategoriesWithAccounts(),
        repository.getActiveAccountsWithCategory(),
        repository.getAllTransactions(),
        repository.getUserProfile(),
        repository.getAllCurrencyRates()
    ) { categoriesWithAccounts, accountsWithCat, transactions, profile, rates ->
        val categoryItems = categoriesWithAccounts.map { cwa ->
            val activeAccounts = cwa.accounts.filter { !it.isArchived }
            val total = activeAccounts.sumOf { it.currentBalance }
            val initialTotal = activeAccounts.sumOf { it.initialBalance }
            HomeCategoryItem(
                category = cwa.category,
                accountsCount = activeAccounts.size,
                totalBalance = total,
                isProfit = total >= initialTotal
            )
        }

        val totalNetWorth = accountsWithCat.sumOf { it.account.currentBalance }

        // Start of current calendar month in millis
        val startOfMonthCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfMonthMillis = startOfMonthCal.timeInMillis

        // Current month transactions
        val currentMonthTxs = transactions.filter { it.timestamp >= startOfMonthMillis }
        val calculatedIncome = currentMonthTxs
            .filter { it.type == TransactionType.CREDIT }
            .sumOf { it.amount }
        val calculatedExpenses = currentMonthTxs
            .filter { it.type == TransactionType.DEBIT }
            .sumOf { it.amount }
        val calculatedSavings = calculatedIncome - calculatedExpenses

        // Previous month closing net worth
        val hasPriorTransactions = transactions.any { it.timestamp < startOfMonthMillis }
        val priorMonthClosingNetWorth = totalNetWorth - calculatedIncome + calculatedExpenses

        val trendPercentage: Double? = if (hasPriorTransactions && priorMonthClosingNetWorth > 0.0) {
            ((totalNetWorth - priorMonthClosingNetWorth) / priorMonthClosingNetWorth) * 100.0
        } else {
            null
        }

        HomeUiState(
            categories = categoryItems,
            accountsWithCategory = accountsWithCat,
            totalNetWorth = totalNetWorth,
            monthlyTrendPercentage = trendPercentage,
            monthlyIncome = calculatedIncome,
            monthlyExpenses = calculatedExpenses,
            monthlySavings = calculatedSavings,
            userProfile = profile,
            currencyRates = rates
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
        }
    }

    suspend fun searchTitles(query: String): List<String> {
        return if (query.isBlank()) {
            repository.getRecentDistinctTitles().first()
        } else {
            repository.searchDistinctTitles(query).first()
        }
    }

    suspend fun resolveTitleDefaults(title: String): TitleDefaults? {
        return repository.resolveTitleDefaults(title)
    }

    class Factory(private val repository: VittiqRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}

