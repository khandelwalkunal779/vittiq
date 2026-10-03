package com.vittiq.android.data.repository

import androidx.room.withTransaction
import com.vittiq.android.data.dao.AccountDao
import com.vittiq.android.data.dao.CurrencyRateDao
import com.vittiq.android.data.dao.TransactionDao
import com.vittiq.android.data.dao.UserProfileDao
import com.vittiq.android.data.database.VittiqDatabase
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionType
import com.vittiq.android.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface VittiqRepository {
    fun getAllAccounts(): Flow<List<Account>>
    fun getAccountById(id: String): Flow<Account?>
    suspend fun getAccountByIdSync(id: String): Account?
    suspend fun insertAccount(account: Account)
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(account: Account)

    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)

    fun getUserProfile(): Flow<UserProfile?>
    suspend fun updateUserProfile(profile: UserProfile)

    fun getAllCurrencyRates(): Flow<List<CurrencyRate>>
    suspend fun updateCurrencyRate(rate: CurrencyRate)
}

class DefaultVittiqRepository(
    private val database: VittiqDatabase,
    private val accountDao: AccountDao = database.accountDao(),
    private val transactionDao: TransactionDao = database.transactionDao(),
    private val userProfileDao: UserProfileDao = database.userProfileDao(),
    private val currencyRateDao: CurrencyRateDao = database.currencyRateDao()
) : VittiqRepository {

    override fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()

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

    override fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    override fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByAccount(accountId)

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

