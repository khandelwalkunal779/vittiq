package com.vittiq.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRate(
    @PrimaryKey
    val currencyCode: String,
    val symbol: String,
    val rateToInr: Double
)

