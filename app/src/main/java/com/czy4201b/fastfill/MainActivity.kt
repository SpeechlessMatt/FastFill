package com.czy4201b.fastfill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.czy4201b.fastfill.core.theme.FastFillTheme
import com.czy4201b.fastfill.navigation.FastFillApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FastFillTheme {
                FastFillApp()
            }
        }
    }
}