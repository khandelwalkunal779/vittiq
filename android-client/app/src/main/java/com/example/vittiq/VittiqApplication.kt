package com.example.vittiq

import android.app.Application
import com.example.vittiq.data.AppContainer
import com.example.vittiq.data.DefaultAppContainer

class VittiqApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
