package com.vittiq.android.data.backup

import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionCategory
import com.vittiq.android.data.model.UserProfile
import kotlinx.serialization.Serializable

@Serializable
data class BackupMetadata(
    val schemaVersion: Int = 4,
    val exportTimestamp: Long,
    val appVersion: String = "1.0",
    val accountCategoryCount: Int = 0,
    val accountCount: Int = 0,
    val transactionCount: Int = 0,
    val transactionCategoryCount: Int = 0,
    val currencyRateCount: Int = 0,
    val userProfileCount: Int = 0,
    val deviceInfo: String? = null
)

@Serializable
data class BackupPayload(
    val metadata: BackupMetadata,
    val accountCategories: List<AccountCategory>,
    val accounts: List<Account>,
    val transactions: List<Transaction>,
    val transactionCategories: List<TransactionCategory>,
    val currencyRates: List<CurrencyRate>,
    val userProfile: UserProfile?
)

enum class RestoreMode {
    REPLACE,
    MERGE
}

data class MergeResult(
    val addedCategoriesCount: Int = 0,
    val addedAccountsCount: Int = 0,
    val importedTransactionsCount: Int = 0,
    val skippedTransactionsCount: Int = 0
)
