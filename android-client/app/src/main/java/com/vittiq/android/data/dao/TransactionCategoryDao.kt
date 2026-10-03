package com.vittiq.android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vittiq.android.data.model.TransactionCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionCategoryDao {
    @Query("SELECT * FROM transaction_categories WHERE isArchived = 0 ORDER BY displayOrder ASC, name ASC")
    fun getAllActiveCategories(): Flow<List<TransactionCategory>>

    @Query("SELECT * FROM transaction_categories ORDER BY isArchived ASC, displayOrder ASC, name ASC")
    fun getAllCategories(): Flow<List<TransactionCategory>>

    @Query("SELECT * FROM transaction_categories ORDER BY displayOrder ASC, name ASC")
    suspend fun getAllCategoriesSync(): List<TransactionCategory>

    @Query("DELETE FROM transaction_categories")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM transaction_categories")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: TransactionCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<TransactionCategory>)

    @Update
    suspend fun update(category: TransactionCategory)

    @Query("UPDATE transaction_categories SET isArchived = 1 WHERE id = :id")
    suspend fun archiveCategory(id: String)

    @Query("UPDATE transaction_categories SET isArchived = 0 WHERE id = :id")
    suspend fun unarchiveCategory(id: String)

    @Delete
    suspend fun delete(category: TransactionCategory)
}
