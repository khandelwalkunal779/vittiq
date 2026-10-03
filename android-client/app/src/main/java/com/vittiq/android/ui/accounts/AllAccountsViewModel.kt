package com.vittiq.android.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.CategoryWithAccounts
import com.vittiq.android.data.repository.VittiqRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class AllAccountsUiState(
    val categoriesWithAccounts: List<CategoryWithAccounts> = emptyList(),
    val allCategories: List<AccountCategory> = emptyList(),
    val selectedCategoryId: String? = null,
    val totalBalance: Double = 0.0
)

class AllAccountsViewModel(
    private val repository: VittiqRepository
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AllAccountsUiState> = combine(
        repository.getCategoriesWithAccounts(),
        repository.getAllCategories(),
        _selectedCategoryId
    ) { categoriesWithAccounts, allCategories, selectedCatId ->
        val filtered = if (selectedCatId == null) {
            categoriesWithAccounts.map { cwa ->
                cwa.copy(accounts = cwa.accounts.filter { !it.isArchived })
            }
        } else {
            categoriesWithAccounts
                .filter { it.category.id == selectedCatId }
                .map { cwa ->
                    cwa.copy(accounts = cwa.accounts.filter { !it.isArchived })
                }
        }

        val total = filtered.flatMap { it.accounts }.sumOf { it.currentBalance }

        AllAccountsUiState(
            categoriesWithAccounts = filtered,
            allCategories = allCategories,
            selectedCategoryId = selectedCatId,
            totalBalance = total
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AllAccountsUiState()
    )

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

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

    class Factory(private val repository: VittiqRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AllAccountsViewModel::class.java)) {
                return AllAccountsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
