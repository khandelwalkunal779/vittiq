package com.vittiq.android

import android.app.Application
import com.vittiq.android.data.database.VittiqDatabase
import com.vittiq.android.data.repository.DefaultVittiqRepository
import com.vittiq.android.data.repository.VittiqRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class VittiqApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: VittiqDatabase by lazy {
        VittiqDatabase.getDatabase(this, applicationScope)
    }

    val repository: VittiqRepository by lazy {
        DefaultVittiqRepository(database)
    }
}

