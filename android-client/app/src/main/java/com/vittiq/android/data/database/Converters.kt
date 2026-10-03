package com.vittiq.android.data.database

import androidx.room.TypeConverter
import com.vittiq.android.data.model.AccountType
import com.vittiq.android.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromAccountType(type: AccountType?): String? = type?.name

    @TypeConverter
    fun toAccountType(name: String?): AccountType? =
        name?.let { runCatching { AccountType.valueOf(it) }.getOrDefault(AccountType.OTHER) }

    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? = type?.name

    @TypeConverter
    fun toTransactionType(name: String?): TransactionType? =
        name?.let { runCatching { TransactionType.valueOf(it) }.getOrDefault(TransactionType.DEBIT) }
}

