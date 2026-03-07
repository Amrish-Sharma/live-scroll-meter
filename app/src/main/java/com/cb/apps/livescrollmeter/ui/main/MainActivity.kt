package com.cb.apps.livescrollmeter.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cb.apps.livescrollmeter.ui.theme.LiveScrollMeterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LiveScrollMeterTheme {
                MainScreen()
            }
        }
    }
}