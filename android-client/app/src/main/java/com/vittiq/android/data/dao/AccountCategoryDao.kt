package com.vittiq.android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.CategoryWithAccounts
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountCategoryDao {
    @Query("SELECT * FROM account_categories ORDER BY displayOrder ASC, name ASC")
    fun getAllCategories(): Flow<List<AccountCategory>>

    @Transaction
    @Query("SELECT * FROM account_categories ORDER BY displayOrder ASC, name ASC")
    fun getCategoriesWithAccounts(): Flow<List<CategoryWithAccounts>>

    @Query("SELECT * FROM account_categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: String): AccountCategory?

    @Query("SELECT COUNT(*) FROM account_categories")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: AccountCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<AccountCategory>)

    @Update
    suspend fun update(category: AccountCategory)

    @Delete
    suspend fun delete(category: AccountCategory)
}
