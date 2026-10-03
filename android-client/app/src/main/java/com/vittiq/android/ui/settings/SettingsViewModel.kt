package com.vittiq.android.ui.settings

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vittiq.android.data.backup.BackupManager
import com.vittiq.android.data.backup.BackupPayload
import com.vittiq.android.data.backup.RestoreMode
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.CategoryWithAccounts
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.TransactionCategory
import com.vittiq.android.data.model.UserProfile
import com.vittiq.android.data.repository.VittiqRepository
import kotlinx.coroutines.flow.MutableStateFlow
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
    val transactionCategories: List<TransactionCategory> = emptyList(),
    val isBackupLoading: Boolean = false,
    val backupMessage: String? = null,
    val pendingRestorePayload: BackupPayload? = null
)

class SettingsViewModel(
    private val repository: VittiqRepository
) : ViewModel() {

    private val backupManager = BackupManager(repository)
    private val _backupLoading = MutableStateFlow(false)
    private val _backupMessage = MutableStateFlow<String?>(null)
    private val _pendingRestorePayload = MutableStateFlow<BackupPayload?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        repository.getCategoriesWithAccounts(),
        repository.getAllCategories(),
        repository.getAllCurrencyRates(),
        repository.getUserProfile(),
        repository.getAllTransactionCategories(),
        _backupLoading,
        _backupMessage,
        _pendingRestorePayload
    ) { args: Array<Any?> ->
        val cwa = args[0] as List<CategoryWithAccounts>
        val cats = args[1] as List<AccountCategory>
        val rates = args[2] as List<CurrencyRate>
        val profile = args[3] as UserProfile?
        val txCats = args[4] as List<TransactionCategory>
        val loading = args[5] as Boolean
        val message = args[6] as String?
        val pending = args[7] as BackupPayload?
        SettingsUiState(
            categoriesWithAccounts = cwa,
            allCategories = cats,
            currencyRates = rates,
            userProfile = profile,
            transactionCategories = txCats,
            isBackupLoading = loading,
            backupMessage = message,
            pendingRestorePayload = pending
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun exportBackup(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            _backupLoading.value = true
            val result = backupManager.exportBackup(uri, contentResolver)
            _backupLoading.value = false
            result.onSuccess { metadata ->
                _backupMessage.value = "Backup exported successfully (${metadata.transactionCount} transactions, ${metadata.accountCount} accounts)."
            }.onFailure { error ->
                _backupMessage.value = "Export failed: ${error.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    fun prepareRestore(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            _backupLoading.value = true
            val result = backupManager.parseBackupPayload(uri, contentResolver)
            _backupLoading.value = false
            result.onSuccess { payload ->
                _pendingRestorePayload.value = payload
            }.onFailure { error ->
                _backupMessage.value = "Invalid backup: ${error.localizedMessage ?: "Could not read backup file"}"
            }
        }
    }

    fun confirmRestore(mode: RestoreMode) {
        val payload = _pendingRestorePayload.value ?: return
        viewModelScope.launch {
            _backupLoading.value = true
            val result = backupManager.restoreBackup(payload, mode)
            _backupLoading.value = false
            _pendingRestorePayload.value = null
            result.onSuccess { message ->
                _backupMessage.value = message
            }.onFailure { error ->
                _backupMessage.value = "Restore failed: ${error.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    fun dismissRestoreDialog() {
        _pendingRestorePayload.value = null
    }

    fun clearBackupMessage() {
        _backupMessage.value = null
    }

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
