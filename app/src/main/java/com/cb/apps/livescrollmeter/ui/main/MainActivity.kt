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
import com.cb.apps.livescrollmeter.service.accessibility.ScrollAccessibilityService
import com.cb.apps.livescrollmeter.service.overlay.OverlayService
import com.cb.apps.livescrollmeter.ui.dashboard.DashboardScreen
import com.cb.apps.livescrollmeter.ui.onboarding.PermissionScreen
import com.cb.apps.livescrollmeter.ui.onboarding.isAccessibilityServiceEnabled
import com.cb.apps.livescrollmeter.ui.theme.LiveScrollMeterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LiveScrollMeterTheme {
                var permissionsGranted by remember { 
                    mutableStateOf(
                        Settings.canDrawOverlays(this) && 
                        isAccessibilityServiceEnabled(this, ScrollAccessibilityService::class.java)
                    ) 
                }

                if (permissionsGranted) {
                    startOverlayService()
                    DashboardScreen()
                } else {
                    PermissionScreen(onPermissionsGranted = {
                        permissionsGranted = true
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
        // Re-check permissions on resume to refresh UI if user granted them in settings
        val hasOverlay = Settings.canDrawOverlays(this)
        val hasAccessibility = isAccessibilityServiceEnabled(this, ScrollAccessibilityService::class.java)
        
        if (hasOverlay && hasAccessibility) {
            startOverlayService()
        }
    }
}
