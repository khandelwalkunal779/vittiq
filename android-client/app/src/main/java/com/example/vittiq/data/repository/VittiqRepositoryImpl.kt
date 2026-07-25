package com.example.vittiq.data.repository

import androidx.room.withTransaction
import com.example.vittiq.data.db.Account
import com.example.vittiq.data.db.AccountDao
import com.example.vittiq.data.db.AccountType
import com.example.vittiq.data.db.Transaction
import com.example.vittiq.data.db.TransactionDao
import com.example.vittiq.data.db.UserProfile
import com.example.vittiq.data.db.UserProfileDao
import com.example.vittiq.data.db.VittiqDatabase
import kotlinx.coroutines.flow.Flow

class VittiqRepositoryImpl(
    private val database: VittiqDatabase,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val userProfileDao: UserProfileDao
) : VittiqRepository {

    override val allAccountsFlow: Flow<List<Account>> = accountDao.getAllAccountsFlow()
    override val totalNetWorthFlow: Flow<Double?> = accountDao.getTotalNetWorthFlow()
    override val allTransactionsFlow: Flow<List<Transaction>> = transactionDao.getAllTransactionsFlow()
    override val userProfileFlow: Flow<UserProfile?> = userProfileDao.getUserProfileFlow()

    override fun getAccountsByTypeFlow(type: AccountType): Flow<List<Account>> {
        return accountDao.getAccountsByTypeFlow(type)
    }

    override fun getTransactionsByAccountFlow(accountId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByAccountFlow(accountId)
    }

    override fun getTotalBalanceByTypeFlow(type: AccountType): Flow<Double?> {
        return accountDao.getTotalBalanceByTypeFlow(type)
    }

    override suspend fun getAccountById(accountId: String): Account? {
        return accountDao.getAccountById(accountId)
    }

    override suspend fun insertAccount(account: Account) {
        accountDao.insertAccount(account)
    }

    override suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account)
    }

    override suspend fun deleteAccount(accountId: String) {
        accountDao.deleteAccountById(accountId)
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        database.withTransaction {
            transactionDao.insertTransaction(transaction)
            recalculateAccountBalance(transaction.accountId)
        }
    }

    override suspend fun deleteTransaction(transactionId: String) {
        database.withTransaction {
            val tx = transactionDao.getTransactionById(transactionId)
            if (tx != null) {
                transactionDao.deleteTransactionById(transactionId)
                recalculateAccountBalance(tx.accountId)
            }
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.updateUserProfile(userProfile)
    }

    override suspend fun recalculateAccountBalance(accountId: String) {
        val account = accountDao.getAccountById(accountId) ?: return
        val credits = transactionDao.getTotalCredits(accountId) ?: 0.0
        val debits = transactionDao.getTotalDebits(accountId) ?: 0.0
        val newBalance = account.initialBalance + credits - debits
        accountDao.updateBalance(accountId, newBalance)
    }
}
