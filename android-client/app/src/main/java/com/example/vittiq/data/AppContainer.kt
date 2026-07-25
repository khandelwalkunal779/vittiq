package com.example.vittiq.data

import android.content.Context
import com.example.vittiq.data.db.VittiqDatabase
import com.example.vittiq.data.repository.VittiqRepository
import com.example.vittiq.data.repository.VittiqRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface AppContainer {
    val repository: VittiqRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val database: VittiqDatabase by lazy {
        VittiqDatabase.getDatabase(context, applicationScope)
    }

    override val repository: VittiqRepository by lazy {
        VittiqRepositoryImpl(
            database = database,
            accountDao = database.accountDao(),
            transactionDao = database.transactionDao(),
            userProfileDao = database.userProfileDao()
        )
    }
}
