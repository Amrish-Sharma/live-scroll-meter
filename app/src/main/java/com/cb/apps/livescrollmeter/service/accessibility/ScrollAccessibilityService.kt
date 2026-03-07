package com.cb.apps.livescrollmeter.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import androidx.annotation.RequiresApi
import com.cb.apps.livescrollmeter.domain.manager.SessionManager

class ScrollAccessibilityService : AccessibilityService() {

    private val supportedApps = setOf(
        "com.google.android.youtube",
        "com.instagram.android",
        "com.twitter.android"
    )


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        if (event.packageName == "com.google.android.youtube") {
            Log.d(
                "LSM_DEBUG",
                "Type=${event.eventType} | Class=${event.className} | Text=${event.text}"
            )
        }
    }

    private fun handleWindowChange(packageName: String) {
        if (packageName in supportedApps) {
            SessionManager.startSession(packageName)
        } else {
            SessionManager.endSession()
        }
    }


    private fun handleScroll(event: AccessibilityEvent) {
        val deltaY = event.scrollDeltaY

        if (kotlin.math.abs(deltaY) > 200 && SessionManager.isThrottlePassed()) {
            SessionManager.incrementSwipe()
            Log.d("LSM", "Swipe detected")
        }
    }

    override fun onInterrupt() {
        // Required override
    }
}