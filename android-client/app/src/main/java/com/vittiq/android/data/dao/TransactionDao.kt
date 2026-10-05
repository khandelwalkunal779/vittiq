package com.vittiq.android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vittiq.android.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY timestamp DESC")
    fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE name LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%' 
           OR (description IS NOT NULL AND description LIKE '%' || :query || '%')
        ORDER BY timestamp DESC
    """)
    fun searchTransactions(query: String): Flow<List<Transaction>>

    @Query("SELECT DISTINCT name FROM transactions WHERE name LIKE '%' || :query || '%' ORDER BY timestamp DESC LIMIT 10")
    fun searchDistinctTitles(query: String): Flow<List<String>>

    @Query("SELECT DISTINCT name FROM transactions ORDER BY timestamp DESC LIMIT 20")
    fun getRecentDistinctTitles(): Flow<List<String>>

    @Query("SELECT * FROM transactions WHERE LOWER(TRIM(name)) = LOWER(TRIM(:title))")
    suspend fun getTransactionsByTitle(title: String): List<Transaction>

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    @Update
    suspend fun update(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactionsSync(): List<Transaction>

    @Query("DELETE FROM transactions")
    suspend fun clearAll()

    @Delete
    suspend fun delete(transaction: Transaction)
}
