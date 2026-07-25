package com.example.vittiq.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class TransactionType {
    CREDIT,
    DEBIT
}

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["accountId"]), Index(value = ["dateTime"])]
)
data class Transaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val dateTime: Long = System.currentTimeMillis(),
    val accountId: String,
    val category: String,
    val amount: Double,
    val name: String,
    val description: String,
    val type: TransactionType
)
