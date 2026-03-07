package com.cb.apps.livescrollmeter.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cb.apps.livescrollmeter.service.overlay.OverlayService
import com.cb.apps.livescrollmeter.ui.dashboard.DashboardScreen
import com.cb.apps.livescrollmeter.ui.onboarding.PermissionScreen
import com.cb.apps.livescrollmeter.ui.theme.LiveScrollMeterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LiveScrollMeterTheme {
                var hasPermission by remember { mutableStateOf(Settings.canDrawOverlays(this)) }

                if (hasPermission) {
                    startOverlayService()
                    DashboardScreen()
                } else {
                    PermissionScreen(onPermissionGranted = {
                        hasPermission = true
                        startOverlayService()
                    })
                }
            }
        }
    }

    private fun startOverlayService() {
        if (Settings.canDrawOverlays(this)) {
            val intent = Intent(this, OverlayService::class.java)
            startService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {
            startOverlayService()
        }
    }
}
