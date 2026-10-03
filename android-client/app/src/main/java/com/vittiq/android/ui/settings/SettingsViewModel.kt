package com.vittiq.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.CategoryWithAccounts
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.TransactionCategory
import com.vittiq.android.data.model.UserProfile
import com.vittiq.android.data.repository.VittiqRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class SettingsUiState(
    val categoriesWithAccounts: List<CategoryWithAccounts> = emptyList(),
    val allCategories: List<AccountCategory> = emptyList(),
    val currencyRates: List<CurrencyRate> = emptyList(),
    val userProfile: UserProfile? = null,
    val transactionCategories: List<TransactionCategory> = emptyList()
)

class SettingsViewModel(
    private val repository: VittiqRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        repository.getCategoriesWithAccounts(),
        repository.getAllCategories(),
        repository.getAllCurrencyRates(),
        repository.getUserProfile(),
        repository.getAllTransactionCategories()
    ) { cwa, cats, rates, profile, txCats ->
        SettingsUiState(
            categoriesWithAccounts = cwa,
            allCategories = cats,
            currencyRates = rates,
            userProfile = profile,
            transactionCategories = txCats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun updateCurrencyRate(rate: CurrencyRate) {
        viewModelScope.launch {
            repository.updateCurrencyRate(rate)
        }
    }

    // Account Categories
    fun addCategory(name: String) {
        viewModelScope.launch {
            val maxOrder = uiState.value.allCategories.maxOfOrNull { it.displayOrder } ?: 0
            val category = AccountCategory(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                displayOrder = maxOrder + 1,
                isCustom = true
            )
            repository.insertCategory(category)
        }
    }

    fun updateCategory(category: AccountCategory) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: AccountCategory) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    // Accounts
    fun addAccount(categoryId: String, name: String, initialBalance: Double) {
        viewModelScope.launch {
            val account = Account(
                id = UUID.randomUUID().toString(),
                categoryId = categoryId,
                name = name.trim(),
                initialBalance = initialBalance,
                currentBalance = initialBalance,
                isArchived = false
            )
            repository.insertAccount(account)
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            repository.updateAccount(account)
        }
    }

    fun deleteOrArchiveAccount(accountId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isDeleted = repository.deleteOrArchiveAccount(accountId)
            onResult(isDeleted)
        }
    }

    fun unarchiveAccount(accountId: String) {
        viewModelScope.launch {
            repository.unarchiveAccount(accountId)
        }
    }

    // Transaction Categories
    fun addTransactionCategory(name: String) {
        viewModelScope.launch {
            val maxOrder = uiState.value.transactionCategories.maxOfOrNull { it.displayOrder } ?: 0
            val txCat = TransactionCategory(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                displayOrder = maxOrder + 1,
                isArchived = false,
                isDefault = false
            )
            repository.insertTransactionCategory(txCat)
        }
    }

    fun updateTransactionCategory(category: TransactionCategory) {
        viewModelScope.launch {
            repository.updateTransactionCategory(category)
        }
    }

    fun archiveTransactionCategory(id: String) {
        viewModelScope.launch {
            repository.archiveTransactionCategory(id)
        }
    }

    fun unarchiveTransactionCategory(id: String) {
        viewModelScope.launch {
            repository.unarchiveTransactionCategory(id)
        }
    }

    class Factory(private val repository: VittiqRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
