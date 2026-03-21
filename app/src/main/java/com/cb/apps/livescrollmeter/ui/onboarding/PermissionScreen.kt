package com.cb.apps.livescrollmeter.ui.onboarding

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cb.apps.livescrollmeter.service.accessibility.ScrollAccessibilityService

@Composable
fun PermissionScreen(onPermissionsGranted: () -> Unit) {
    val context = LocalContext.current
    val hasOverlay = Settings.canDrawOverlays(context)
    val hasAccessibility = isAccessibilityServiceEnabled(context, ScrollAccessibilityService::class.java)

    if (hasOverlay && hasAccessibility) {
        onPermissionsGranted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Permissions Required",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Live Scroll Meter needs permissions to display metrics and detect scrolls.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!hasOverlay) {
            Button(
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            ) {
                Text("Grant Overlay Permission")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!hasAccessibility) {
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }
            ) {
                Text("Grant Accessibility Permission")
            }
        }
    }
}

fun isAccessibilityServiceEnabled(context: Context, service: Class<*>): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
    for (enabledService in enabledServices) {
        val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
        if (enabledServiceInfo.packageName == context.packageName && enabledServiceInfo.name == service.name) {
            return true
        }
    }
    return false
}
