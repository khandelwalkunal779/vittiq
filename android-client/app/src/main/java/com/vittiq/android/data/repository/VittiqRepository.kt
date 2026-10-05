package com.vittiq.android.data.repository

import androidx.room.withTransaction
import com.vittiq.android.data.dao.AccountCategoryDao
import com.vittiq.android.data.dao.AccountDao
import com.vittiq.android.data.dao.CurrencyRateDao
import com.vittiq.android.data.dao.TransactionCategoryDao
import com.vittiq.android.data.dao.TransactionDao
import com.vittiq.android.data.dao.UserProfileDao
import com.vittiq.android.data.database.VittiqDatabase
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.AccountWithCategory
import com.vittiq.android.data.model.CategoryWithAccounts
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.TitleDefaults
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionCategory
import com.vittiq.android.data.model.TransactionType
import com.vittiq.android.data.model.UserProfile
import com.vittiq.android.data.backup.BackupMetadata
import com.vittiq.android.data.backup.BackupPayload
import com.vittiq.android.data.backup.MergeResult
import kotlinx.coroutines.flow.Flow

interface VittiqRepository {
    // Categories
    fun getAllCategories(): Flow<List<AccountCategory>>
    fun getCategoriesWithAccounts(): Flow<List<CategoryWithAccounts>>
    suspend fun getCategoryById(id: String): AccountCategory?
    suspend fun insertCategory(category: AccountCategory)
    suspend fun updateCategory(category: AccountCategory)
    suspend fun deleteCategory(category: AccountCategory)

    // Accounts
    fun getAllActiveAccounts(): Flow<List<Account>>
    fun getAllAccounts(): Flow<List<Account>>
    fun getActiveAccountsWithCategory(): Flow<List<AccountWithCategory>>
    fun getActiveAccountsByCategoryId(categoryId: String): Flow<List<Account>>
    fun getAllAccountsByCategoryId(categoryId: String): Flow<List<Account>>
    fun getAccountById(id: String): Flow<Account?>
    suspend fun getAccountByIdSync(id: String): Account?
    suspend fun insertAccount(account: Account)
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    suspend fun archiveAccount(id: String)
    suspend fun unarchiveAccount(id: String)
    suspend fun deleteOrArchiveAccount(accountId: String): Boolean

    // Transactions
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>>
    fun searchTransactions(query: String): Flow<List<Transaction>>
    fun searchDistinctTitles(query: String): Flow<List<String>>
    fun getRecentDistinctTitles(): Flow<List<String>>
    suspend fun getTransactionsByTitle(title: String): List<Transaction>
    suspend fun resolveTitleDefaults(title: String): TitleDefaults?
    suspend fun addTransaction(transaction: Transaction)
    suspend fun updateTransaction(oldTransaction: Transaction, newTransaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)

    // Transaction Categories
    fun getAllActiveTransactionCategories(): Flow<List<TransactionCategory>>
    fun getAllTransactionCategories(): Flow<List<TransactionCategory>>
    suspend fun insertTransactionCategory(category: TransactionCategory)
    suspend fun updateTransactionCategory(category: TransactionCategory)
    suspend fun archiveTransactionCategory(id: String)
    suspend fun unarchiveTransactionCategory(id: String)
    suspend fun deleteTransactionCategory(category: TransactionCategory)

    // Profile
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun updateUserProfile(profile: UserProfile)

    // Currency Rates
    fun getAllCurrencyRates(): Flow<List<CurrencyRate>>
    suspend fun updateCurrencyRate(rate: CurrencyRate)

    // Backup & Restore
    suspend fun getBackupPayload(): BackupPayload
    suspend fun restoreReplace(payload: BackupPayload)
    suspend fun restoreMerge(payload: BackupPayload): MergeResult
}

class DefaultVittiqRepository(
    private val database: VittiqDatabase,
    private val categoryDao: AccountCategoryDao = database.accountCategoryDao(),
    private val accountDao: AccountDao = database.accountDao(),
    private val transactionDao: TransactionDao = database.transactionDao(),
    private val transactionCategoryDao: TransactionCategoryDao = database.transactionCategoryDao(),
    private val userProfileDao: UserProfileDao = database.userProfileDao(),
    private val currencyRateDao: CurrencyRateDao = database.currencyRateDao()
) : VittiqRepository {

    override fun getAllCategories(): Flow<List<AccountCategory>> = categoryDao.getAllCategories()

    override fun getCategoriesWithAccounts(): Flow<List<CategoryWithAccounts>> =
        categoryDao.getCategoriesWithAccounts()

    override suspend fun getCategoryById(id: String): AccountCategory? =
        categoryDao.getCategoryById(id)

    override suspend fun insertCategory(category: AccountCategory) {
        categoryDao.insert(category)
    }

    override suspend fun updateCategory(category: AccountCategory) {
        categoryDao.update(category)
    }

    override suspend fun deleteCategory(category: AccountCategory) {
        categoryDao.delete(category)
    }

    override fun getAllActiveAccounts(): Flow<List<Account>> = accountDao.getAllActiveAccounts()

    override fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()

    override fun getActiveAccountsWithCategory(): Flow<List<AccountWithCategory>> =
        accountDao.getActiveAccountsWithCategory()

    override fun getActiveAccountsByCategoryId(categoryId: String): Flow<List<Account>> =
        accountDao.getActiveAccountsByCategoryId(categoryId)

    override fun getAllAccountsByCategoryId(categoryId: String): Flow<List<Account>> =
        accountDao.getAllAccountsByCategoryId(categoryId)

    override fun getAccountById(id: String): Flow<Account?> = accountDao.getAccountById(id)

    override suspend fun getAccountByIdSync(id: String): Account? = accountDao.getAccountByIdSync(id)

    override suspend fun insertAccount(account: Account) {
        accountDao.insert(account)
    }

    override suspend fun updateAccount(account: Account) {
        accountDao.update(account)
    }

    override suspend fun deleteAccount(account: Account) {
        accountDao.delete(account)
    }

    override suspend fun archiveAccount(id: String) {
        accountDao.archiveAccount(id)
    }

    override suspend fun unarchiveAccount(id: String) {
        accountDao.unarchiveAccount(id)
    }

    override suspend fun deleteOrArchiveAccount(accountId: String): Boolean {
        val txCount = accountDao.getTransactionCountForAccount(accountId)
        return if (txCount > 0) {
            accountDao.archiveAccount(accountId)
            false // archived
        } else {
            val acc = accountDao.getAccountByIdSync(accountId)
            if (acc != null) {
                accountDao.delete(acc)
            }
            true // deleted
        }
    }

    override fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    override fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByAccount(accountId)

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        transactionDao.searchTransactions(query)

    override fun searchDistinctTitles(query: String): Flow<List<String>> =
        transactionDao.searchDistinctTitles(query)

    override fun getRecentDistinctTitles(): Flow<List<String>> =
        transactionDao.getRecentDistinctTitles()

    override suspend fun getTransactionsByTitle(title: String): List<Transaction> =
        transactionDao.getTransactionsByTitle(title)

    override suspend fun resolveTitleDefaults(title: String): TitleDefaults? {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return null
        val txs = transactionDao.getTransactionsByTitle(trimmed)
        if (txs.isEmpty()) return null

        val dominantType = txs.groupingBy { it.type }.eachCount().maxByOrNull { it.value }?.key ?: TransactionType.DEBIT
        val typeFiltered = txs.filter { it.type == dominantType }

        return if (dominantType == TransactionType.TRANSFER) {
            val mostFrequentPair = typeFiltered
                .groupingBy { Pair(it.accountId, it.toAccountId) }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key

            if (mostFrequentPair != null && mostFrequentPair.second != null) {
                TitleDefaults(
                    title = trimmed,
                    type = TransactionType.TRANSFER,
                    primaryAccountId = mostFrequentPair.first,
                    toAccountId = mostFrequentPair.second
                )
            } else {
                null
            }
        } else {
            val mostFrequentAccount = typeFiltered
                .groupingBy { it.accountId }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key

            if (mostFrequentAccount != null) {
                TitleDefaults(
                    title = trimmed,
                    type = dominantType,
                    primaryAccountId = mostFrequentAccount,
                    toAccountId = null
                )
            } else {
                null
            }
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        database.withTransaction {
            transactionDao.insert(transaction)
            when (transaction.type) {
                TransactionType.TRANSFER -> {
                    // Debit from source account
                    accountDao.adjustBalance(transaction.accountId, -transaction.amount)
                    // Credit to destination account
                    transaction.toAccountId?.let { toAccId ->
                        accountDao.adjustBalance(toAccId, transaction.amount)
                    }
                }
                TransactionType.CREDIT -> {
                    accountDao.adjustBalance(transaction.accountId, transaction.amount)
                }
                TransactionType.DEBIT -> {
                    accountDao.adjustBalance(transaction.accountId, -transaction.amount)
                }
            }
        }
    }

    override suspend fun updateTransaction(oldTransaction: Transaction, newTransaction: Transaction) {
        database.withTransaction {
            // 1. Revert old transaction's balance effects
            when (oldTransaction.type) {
                TransactionType.TRANSFER -> {
                    accountDao.adjustBalance(oldTransaction.accountId, oldTransaction.amount)
                    oldTransaction.toAccountId?.let { toAccId ->
                        accountDao.adjustBalance(toAccId, -oldTransaction.amount)
                    }
                }
                TransactionType.CREDIT -> {
                    accountDao.adjustBalance(oldTransaction.accountId, -oldTransaction.amount)
                }
                TransactionType.DEBIT -> {
                    accountDao.adjustBalance(oldTransaction.accountId, oldTransaction.amount)
                }
            }

            // 2. Apply new transaction's balance effects
            when (newTransaction.type) {
                TransactionType.TRANSFER -> {
                    accountDao.adjustBalance(newTransaction.accountId, -newTransaction.amount)
                    newTransaction.toAccountId?.let { toAccId ->
                        accountDao.adjustBalance(toAccId, newTransaction.amount)
                    }
                }
                TransactionType.CREDIT -> {
                    accountDao.adjustBalance(newTransaction.accountId, newTransaction.amount)
                }
                TransactionType.DEBIT -> {
                    accountDao.adjustBalance(newTransaction.accountId, -newTransaction.amount)
                }
            }

            // 3. Update the record
            transactionDao.update(newTransaction)
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        database.withTransaction {
            transactionDao.delete(transaction)
            when (transaction.type) {
                TransactionType.TRANSFER -> {
                    // Revert source debit (+amount)
                    accountDao.adjustBalance(transaction.accountId, transaction.amount)
                    // Revert destination credit (-amount)
                    transaction.toAccountId?.let { toAccId ->
                        accountDao.adjustBalance(toAccId, -transaction.amount)
                    }
                }
                TransactionType.CREDIT -> {
                    accountDao.adjustBalance(transaction.accountId, -transaction.amount)
                }
                TransactionType.DEBIT -> {
                    accountDao.adjustBalance(transaction.accountId, transaction.amount)
                }
            }
        }
    }

    override fun getAllActiveTransactionCategories(): Flow<List<TransactionCategory>> =
        transactionCategoryDao.getAllActiveCategories()

    override fun getAllTransactionCategories(): Flow<List<TransactionCategory>> =
        transactionCategoryDao.getAllCategories()

    override suspend fun insertTransactionCategory(category: TransactionCategory) {
        transactionCategoryDao.insert(category)
    }

    override suspend fun updateTransactionCategory(category: TransactionCategory) {
        transactionCategoryDao.update(category)
    }

    override suspend fun archiveTransactionCategory(id: String) {
        transactionCategoryDao.archiveCategory(id)
    }

    override suspend fun unarchiveTransactionCategory(id: String) {
        transactionCategoryDao.unarchiveCategory(id)
    }

    override suspend fun deleteTransactionCategory(category: TransactionCategory) {
        transactionCategoryDao.delete(category)
    }

    override fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()

    override suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.update(profile)
    }

    override fun getAllCurrencyRates(): Flow<List<CurrencyRate>> = currencyRateDao.getAllRates()

    override suspend fun updateCurrencyRate(rate: CurrencyRate) {
        currencyRateDao.updateRate(rate)
    }

    override suspend fun getBackupPayload(): BackupPayload {
        val categories = categoryDao.getAllCategoriesSync()
        val accounts = accountDao.getAllAccountsSync()
        val transactions = transactionDao.getAllTransactionsSync()
        val txCategories = transactionCategoryDao.getAllCategoriesSync()
        val rates = currencyRateDao.getAllRatesSync()
        val profile = userProfileDao.getUserProfileSync()

        val metadata = BackupMetadata(
            schemaVersion = 4,
            exportTimestamp = System.currentTimeMillis(),
            appVersion = "1.0",
            accountCategoryCount = categories.size,
            accountCount = accounts.size,
            transactionCount = transactions.size,
            transactionCategoryCount = txCategories.size,
            currencyRateCount = rates.size,
            userProfileCount = if (profile != null) 1 else 0
        )

        return BackupPayload(
            metadata = metadata,
            accountCategories = categories,
            accounts = accounts,
            transactions = transactions,
            transactionCategories = txCategories,
            currencyRates = rates,
            userProfile = profile
        )
    }

    override suspend fun restoreReplace(payload: BackupPayload) {
        database.withTransaction {
            // Delete child tables first to avoid foreign key issues
            transactionDao.clearAll()
            accountDao.clearAll()
            categoryDao.clearAll()
            transactionCategoryDao.clearAll()
            currencyRateDao.clearAll()
            userProfileDao.clearAll()

            // Insert in parent-to-child order
            categoryDao.insertAll(payload.accountCategories)
            accountDao.insertAll(payload.accounts)
            transactionCategoryDao.insertAll(payload.transactionCategories)
            transactionDao.insertAll(payload.transactions)
            currencyRateDao.insertAll(payload.currencyRates)
            payload.userProfile?.let { userProfileDao.insert(it) }
        }
    }

    override suspend fun restoreMerge(payload: BackupPayload): MergeResult {
        return database.withTransaction {
            var addedCategories = 0
            var addedAccounts = 0
            var importedTransactions = 0
            var skippedTransactions = 0

            // 1. Account Categories: match by name
            val localCategories = categoryDao.getAllCategoriesSync().toMutableList()
            val categoryIdMap = mutableMapOf<String, String>()

            for (backupCat in payload.accountCategories) {
                val existing = localCategories.find { it.name.equals(backupCat.name, ignoreCase = true) }
                if (existing != null) {
                    categoryIdMap[backupCat.id] = existing.id
                } else {
                    categoryDao.insert(backupCat)
                    localCategories.add(backupCat)
                    categoryIdMap[backupCat.id] = backupCat.id
                    addedCategories++
                }
            }

            // 2. Transaction Categories: match by name
            val localTxCategories = transactionCategoryDao.getAllCategoriesSync().toMutableList()
            for (backupTxCat in payload.transactionCategories) {
                val exists = localTxCategories.any { it.name.equals(backupTxCat.name, ignoreCase = true) }
                if (!exists) {
                    transactionCategoryDao.insert(backupTxCat)
                    localTxCategories.add(backupTxCat)
                }
            }

            // 3. Accounts: match by name and resolved categoryId
            val localAccounts = accountDao.getAllAccountsSync().toMutableList()
            val accountIdMap = mutableMapOf<String, String>()

            for (backupAcc in payload.accounts) {
                val resolvedCatId = categoryIdMap[backupAcc.categoryId] ?: backupAcc.categoryId
                val existing = localAccounts.find {
                    it.name.equals(backupAcc.name, ignoreCase = true) && it.categoryId == resolvedCatId
                }
                if (existing != null) {
                    accountIdMap[backupAcc.id] = existing.id
                } else {
                    val newAcc = backupAcc.copy(
                        categoryId = resolvedCatId,
                        currentBalance = backupAcc.initialBalance
                    )
                    accountDao.insert(newAcc)
                    localAccounts.add(newAcc)
                    accountIdMap[backupAcc.id] = newAcc.id
                    addedAccounts++
                }
            }

            // 4. Transactions: deduplicate and insert
            val localTransactions = transactionDao.getAllTransactionsSync()
            val sortedBackupTransactions = payload.transactions.sortedBy { it.timestamp }

            for (backupTx in sortedBackupTransactions) {
                val isDuplicate = localTransactions.any { localTx ->
                    localTx.id == backupTx.id || (
                        localTx.timestamp == backupTx.timestamp &&
                        localTx.amount == backupTx.amount &&
                        localTx.name.equals(backupTx.name, ignoreCase = true)
                    )
                }

                if (isDuplicate) {
                    skippedTransactions++
                    continue
                }

                val resolvedAccId = accountIdMap[backupTx.accountId] ?: backupTx.accountId
                val resolvedToAccId = backupTx.toAccountId?.let { accountIdMap[it] ?: it }

                val newTx = backupTx.copy(
                    accountId = resolvedAccId,
                    toAccountId = resolvedToAccId
                )

                transactionDao.insert(newTx)
                importedTransactions++

                when (newTx.type) {
                    TransactionType.CREDIT -> {
                        accountDao.adjustBalance(newTx.accountId, newTx.amount)
                    }
                    TransactionType.DEBIT -> {
                        accountDao.adjustBalance(newTx.accountId, -newTx.amount)
                    }
                    TransactionType.TRANSFER -> {
                        accountDao.adjustBalance(newTx.accountId, -newTx.amount)
                        newTx.toAccountId?.let { toId ->
                            accountDao.adjustBalance(toId, newTx.amount)
                        }
                    }
                }
            }

            // 5. Overwrite User Profile and Currency Rates with backup values
            payload.userProfile?.let { userProfileDao.insert(it) }
            if (payload.currencyRates.isNotEmpty()) {
                currencyRateDao.insertAll(payload.currencyRates)
            }

            MergeResult(
                addedCategoriesCount = addedCategories,
                addedAccountsCount = addedAccounts,
                importedTransactionsCount = importedTransactions,
                skippedTransactionsCount = skippedTransactions
            )
        }
    }
}
