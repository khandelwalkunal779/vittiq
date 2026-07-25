package com.example.vittiq.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY name ASC")
    fun getAllAccountsFlow(): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE type = :type ORDER BY name ASC")
    fun getAccountsByTypeFlow(type: AccountType): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    suspend fun getAccountById(accountId: String): Account?

    @Query("SELECT SUM(currentBalance) FROM accounts")
    fun getTotalNetWorthFlow(): Flow<Double?>

    @Query("SELECT SUM(currentBalance) FROM accounts WHERE type = :type")
    fun getTotalBalanceByTypeFlow(type: AccountType): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    @Query("DELETE FROM accounts WHERE id = :accountId")
    suspend fun deleteAccountById(accountId: String)

    @Query("UPDATE accounts SET currentBalance = :newBalance WHERE id = :accountId")
    suspend fun updateBalance(accountId: String, newBalance: Double)
}
