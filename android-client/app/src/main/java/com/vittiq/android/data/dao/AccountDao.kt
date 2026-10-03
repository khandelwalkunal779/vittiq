package com.vittiq.android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts WHERE isArchived = 0 ORDER BY name ASC")
    fun getAllActiveAccounts(): Flow<List<Account>>

    @Query("SELECT * FROM accounts ORDER BY isArchived ASC, name ASC")
    fun getAllAccounts(): Flow<List<Account>>

    @Transaction
    @Query("SELECT * FROM accounts WHERE isArchived = 0 ORDER BY name ASC")
    fun getActiveAccountsWithCategory(): Flow<List<AccountWithCategory>>

    @Query("SELECT * FROM accounts WHERE categoryId = :categoryId AND isArchived = 0 ORDER BY name ASC")
    fun getActiveAccountsByCategoryId(categoryId: String): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE categoryId = :categoryId ORDER BY isArchived ASC, name ASC")
    fun getAllAccountsByCategoryId(categoryId: String): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccountById(id: String): Flow<Account?>

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountByIdSync(id: String): Account?

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM transactions WHERE accountId = :accountId")
    suspend fun getTransactionCountForAccount(accountId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<Account>)

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)

    @Query("UPDATE accounts SET isArchived = 1 WHERE id = :id")
    suspend fun archiveAccount(id: String)

    @Query("UPDATE accounts SET isArchived = 0 WHERE id = :id")
    suspend fun unarchiveAccount(id: String)

    @Query("UPDATE accounts SET currentBalance = :newBalance WHERE id = :accountId")
    suspend fun updateBalance(accountId: String, newBalance: Double)

    @Query("UPDATE accounts SET currentBalance = currentBalance + :delta WHERE id = :accountId")
    suspend fun adjustBalance(accountId: String, delta: Double)
}

