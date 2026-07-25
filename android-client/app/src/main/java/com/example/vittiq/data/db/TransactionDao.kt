package com.example.vittiq.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY dateTime DESC")
    fun getTransactionsByAccountFlow(accountId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: String)

    @Query("SELECT SUM(amount) FROM transactions WHERE accountId = :accountId AND type = 'CREDIT'")
    suspend fun getTotalCredits(accountId: String): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE accountId = :accountId AND type = 'DEBIT'")
    suspend fun getTotalDebits(accountId: String): Double?
}
