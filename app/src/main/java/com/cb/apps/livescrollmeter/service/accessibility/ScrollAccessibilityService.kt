package com.cb.apps.livescrollmeter.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
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
        "com.twitter.android",
        "com.x.android"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val packageName = event.packageName?.toString() ?: return
        if (packageName !in supportedApps) {
            sessionManager.endSession()
            return
        }

        val rootNode = rootInActiveWindow ?: return

        val isInShortsFeed = detectShortsFeed(packageName, rootNode)
        
        if (isInShortsFeed) {
            sessionManager.startSession(packageName)
            handleSwipeDetection(event, rootNode)
        } else {
            // Only end session if we are sure we are not in a short feed but still in the app
            // Some events might not have the full hierarchy, so we be careful
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                 sessionManager.endSession()
            }
        }
    }

    private fun detectShortsFeed(packageName: String, rootNode: AccessibilityNodeInfo): Boolean {
        return when (packageName) {
            "com.google.android.youtube" -> {
                val ids = listOf(
                    "com.google.android.youtube:id/reel_recycler",
                    "com.google.android.youtube:id/shorts_player_view",
                    "com.google.android.youtube:id/reel_container"
                )
                ids.any { rootNode.findAccessibilityNodeInfosByViewId(it).isNotEmpty() } ||
                // Fallback: Check if there's a node with "Shorts" in content description or text
                searchForText(rootNode, "Shorts")
            }
            "com.instagram.android" -> {
                val ids = listOf(
                    "com.instagram.android:id/reels_video_container",
                    "com.instagram.android:id/reels_viewpager"
                )
                ids.any { rootNode.findAccessibilityNodeInfosByViewId(it).isNotEmpty() }
            }
            "com.twitter.android", "com.x.android" -> {
                val ids = listOf(
                    "com.twitter.android:id/video_container",
                    "com.twitter.android:id/reels_container" // Hypothetical
                )
                ids.any { rootNode.findAccessibilityNodeInfosByViewId(it).isNotEmpty() }
            }
            else -> false
        }
    }

    private fun searchForText(node: AccessibilityNodeInfo?, text: String): Boolean {
        if (node == null) return false
        if (node.text?.toString()?.contains(text, ignoreCase = true) == true ||
            node.contentDescription?.toString()?.contains(text, ignoreCase = true) == true) {
            return true
        }
        for (i in 0 until node.childCount) {
            if (searchForText(node.getChild(i), text)) return true
        }
        return false
    }

    private fun handleSwipeDetection(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo) {
        // Log for debugging
        Log.d("LSM_DEBUG", "Event: ${AccessibilityEvent.eventTypeToString(event.eventType)} Source: ${event.className}")

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED,
            AccessibilityEvent.TYPE_VIEW_SELECTED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                // Check if content identifier changed
                if (sessionManager.checkContentChanged(rootNode)) {
                    sessionManager.incrementSwipe()
                    Log.d("LSM_DEBUG", "Swipe detected! Total: ${sessionManager.swipeCount.value}")
                }
            }
        }
    }

    override fun onInterrupt() {
    }
}
