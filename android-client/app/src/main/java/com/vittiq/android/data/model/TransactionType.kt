package com.vittiq.android.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    CREDIT,
    DEBIT,
    TRANSFER
}
