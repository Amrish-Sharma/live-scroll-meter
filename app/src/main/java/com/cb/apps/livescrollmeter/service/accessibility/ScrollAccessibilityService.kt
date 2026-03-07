package com.cb.apps.livescrollmeter.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.cb.apps.livescrollmeter.domain.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScrollAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var sessionManager: SessionManager

    private val supportedApps = setOf(
        "com.google.android.youtube",
        "com.instagram.android",
        "com.twitter.android"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val packageName = event.packageName?.toString() ?: return
        val eventType = event.eventType

        // Handle app window change
        handleWindowChange(packageName)

        // Heuristic swipe/content change detection
        if (packageName in supportedApps) {
            when (eventType) {
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                    val deltaY = event.scrollDeltaY
                    if (kotlin.math.abs(deltaY) > 200 && sessionManager.isThrottlePassed()) {
                        sessionManager.incrementSwipe()
                        Log.d("LSM", "Swipe detected (scrollDeltaY)")
                    }
                }
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
                AccessibilityEvent.TYPE_VIEW_SELECTED,
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                    if (sessionManager.isHeuristicSwipe(event)) {
                        sessionManager.incrementSwipe()
                        Log.d("LSM", "Swipe detected (heuristic)")
                    }
                }
            }
        }
    }

    private fun handleWindowChange(packageName: String) {
        if (packageName in supportedApps) {
            sessionManager.startSession(packageName)
        } else {
            sessionManager.endSession()
        }
    }

    override fun onInterrupt() {
    }
}
