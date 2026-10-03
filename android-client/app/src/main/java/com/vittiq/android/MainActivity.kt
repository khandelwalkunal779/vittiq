package com.vittiq.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vittiq.android.theme.BrightSnow
import com.vittiq.android.theme.VittiqTheme
import com.vittiq.android.ui.VittiqApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as VittiqApplication).repository

        setContent {
            VittiqTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BrightSnow
                ) {
                    VittiqApp(repository = repository)
                }
            }
        }
    }
}

