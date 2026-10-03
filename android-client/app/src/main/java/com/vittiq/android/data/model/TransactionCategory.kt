package com.vittiq.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transaction_categories")
data class TransactionCategory(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconName: String = "Category",
    val displayOrder: Int = 0,
    val isArchived: Boolean = false,
    val isDefault: Boolean = false
)
