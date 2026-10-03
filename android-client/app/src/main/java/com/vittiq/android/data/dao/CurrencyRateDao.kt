package com.vittiq.android.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vittiq.android.data.model.CurrencyRate
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyRateDao {
    @Query("SELECT * FROM currency_rates ORDER BY currencyCode ASC")
    fun getAllRates(): Flow<List<CurrencyRate>>

    @Query("SELECT * FROM currency_rates WHERE currencyCode = :code LIMIT 1")
    suspend fun getRateByCode(code: String): CurrencyRate?

    @Query("SELECT COUNT(*) FROM currency_rates")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<CurrencyRate>)

    @Query("SELECT * FROM currency_rates ORDER BY currencyCode ASC")
    suspend fun getAllRatesSync(): List<CurrencyRate>

    @Query("DELETE FROM currency_rates")
    suspend fun clearAll()

    @Update
    suspend fun updateRate(rate: CurrencyRate)
}

