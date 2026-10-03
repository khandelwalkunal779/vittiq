package com.vittiq.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "account_categories")
data class AccountCategory(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val displayOrder: Int = 0,
    val isCustom: Boolean = false
)
