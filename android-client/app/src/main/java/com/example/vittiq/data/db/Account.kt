package com.example.vittiq.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class AccountType {
    CASH,
    BANK_ACCOUNT,
    CARD,
    INVESTMENT
}

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: AccountType,
    val initialBalance: Double,
    val currentBalance: Double
)
