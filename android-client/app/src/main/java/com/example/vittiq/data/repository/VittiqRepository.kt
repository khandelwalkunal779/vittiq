package com.example.vittiq.data.repository

import com.example.vittiq.data.db.Account
import com.example.vittiq.data.db.AccountType
import com.example.vittiq.data.db.Transaction
import com.example.vittiq.data.db.UserProfile
import kotlinx.coroutines.flow.Flow

interface VittiqRepository {
    val allAccountsFlow: Flow<List<Account>>
    val totalNetWorthFlow: Flow<Double?>
    val allTransactionsFlow: Flow<List<Transaction>>
    val userProfileFlow: Flow<UserProfile?>

    fun getAccountsByTypeFlow(type: AccountType): Flow<List<Account>>
    fun getTransactionsByAccountFlow(accountId: String): Flow<List<Transaction>>
    fun getTotalBalanceByTypeFlow(type: AccountType): Flow<Double?>

    suspend fun getAccountById(accountId: String): Account?
    suspend fun insertAccount(account: Account)
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(accountId: String)

    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transactionId: String)

    suspend fun updateUserProfile(userProfile: UserProfile)
    suspend fun recalculateAccountBalance(accountId: String)
}
