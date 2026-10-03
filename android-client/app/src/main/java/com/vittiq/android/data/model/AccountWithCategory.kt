package com.vittiq.android.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class AccountWithCategory(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: AccountCategory
)
