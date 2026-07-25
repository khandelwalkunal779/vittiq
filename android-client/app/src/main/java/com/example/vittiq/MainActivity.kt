package com.example.vittiq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vittiq.ui.navigation.VittiqNavGraph
import com.example.vittiq.ui.theme.VittiqTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VittiqTheme {
                VittiqNavGraph()
            }
        }
    }
}