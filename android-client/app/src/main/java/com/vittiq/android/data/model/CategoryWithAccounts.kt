package com.vittiq.android.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithAccounts(
    @Embedded val category: AccountCategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val accounts: List<Account>
)
