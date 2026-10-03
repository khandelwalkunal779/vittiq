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
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionCategory
import com.vittiq.android.data.model.TransactionType
import com.vittiq.android.data.model.UserProfile
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
}
