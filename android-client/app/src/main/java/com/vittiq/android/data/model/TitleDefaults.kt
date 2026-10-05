package com.vittiq.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TitleDefaults(
    val title: String,
    val type: TransactionType,
    val primaryAccountId: String,
    val toAccountId: String? = null
)
