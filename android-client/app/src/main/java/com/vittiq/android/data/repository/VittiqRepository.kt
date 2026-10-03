package com.vittiq.android.data.repository

import androidx.room.withTransaction
import com.vittiq.android.data.dao.AccountCategoryDao
import com.vittiq.android.data.dao.AccountDao
import com.vittiq.android.data.dao.CurrencyRateDao
import com.vittiq.android.data.dao.TransactionDao
import com.vittiq.android.data.dao.UserProfileDao
import com.vittiq.android.data.database.VittiqDatabase
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.AccountWithCategory
import com.vittiq.android.data.model.CategoryWithAccounts
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.Transaction
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
    suspend fun deleteTransaction(transaction: Transaction)

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
            // Atomically adjust account balance
            val delta = if (transaction.type == TransactionType.CREDIT) {
                transaction.amount
            } else {
                -transaction.amount
            }
            accountDao.adjustBalance(transaction.accountId, delta)
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        database.withTransaction {
            transactionDao.delete(transaction)
            // Revert balance adjustment
            val delta = if (transaction.type == TransactionType.CREDIT) {
                -transaction.amount
            } else {
                transaction.amount
            }
            accountDao.adjustBalance(transaction.accountId, delta)
        }
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
