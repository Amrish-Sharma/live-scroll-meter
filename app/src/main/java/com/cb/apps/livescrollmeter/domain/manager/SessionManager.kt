package com.cb.apps.livescrollmeter.domain.manager

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.cb.apps.livescrollmeter.data.local.SessionDao
import com.cb.apps.livescrollmeter.data.local.SessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val sessionDao: SessionDao
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentApp: String? = null
    private var lastSwipeTime = 0L

    private val _swipeCount = MutableStateFlow(0)
    val swipeCount: StateFlow<Int> = _swipeCount

    private val _sessionTime = MutableStateFlow(0L)
    val sessionTime: StateFlow<Long> = _sessionTime

    private val _activePackage = MutableStateFlow<String?>(null)
    val activePackage: StateFlow<String?> = _activePackage

    private var lastContentIdentifier: String? = null
    private var timerJob: Job? = null

    fun startSession(packageName: String) {
        if (currentApp == packageName) return

        Log.d("LSM_DEBUG", "Starting session for $packageName")
        currentApp = packageName
        _activePackage.value = packageName
        _swipeCount.value = 0
        _sessionTime.value = 0
        lastContentIdentifier = null
        lastSwipeTime = System.currentTimeMillis() // Set initial time to avoid immediate double-trigger

        startTimer()
    }

    fun endSession() {
        val packageName = currentApp ?: return
        Log.d("LSM_DEBUG", "Ending session for $packageName")
        val count = _swipeCount.value
        val duration = _sessionTime.value

        if (count > 0 || duration > 5) {
            scope.launch {
                sessionDao.insertSession(
                    SessionEntity(
                        packageName = packageName,
                        swipeCount = count,
                        durationSeconds = duration
                    )
                )
            }
        }

        currentApp = null
        _activePackage.value = null
        lastContentIdentifier = null
        stopTimer()
    }

    fun incrementSwipe() {
        _swipeCount.value += 1
        Log.d("LSM_DEBUG", "Swipe incremented. New count: ${_swipeCount.value}")
    }

    fun checkContentChanged(rootNode: AccessibilityNodeInfo): Boolean {
        val currentIdentifier = extractContentIdentifier(rootNode)
        
        if (currentIdentifier == null) return false

        if (currentIdentifier != lastContentIdentifier) {
            val now = System.currentTimeMillis()
            
            // Increased debounce to 1000ms to prevent double counts from overlapping events
            if (now - lastSwipeTime > 1000) {
                if (lastContentIdentifier != null) {
                    Log.d("LSM_DEBUG", "Content changed: $lastContentIdentifier -> $currentIdentifier")
                    lastContentIdentifier = currentIdentifier
                    lastSwipeTime = now
                    return true
                } else {
                    // First content detected in session
                    lastContentIdentifier = currentIdentifier
                    lastSwipeTime = now
                    Log.d("LSM_DEBUG", "Initial content set: $currentIdentifier")
                }
            } else {
                Log.d("LSM_DEBUG", "Content change ignored (throttled): $currentIdentifier")
            }
        }
        return false
    }

    private fun extractContentIdentifier(rootNode: AccessibilityNodeInfo): String? {
        val packageName = currentApp ?: return null
        
        val specificId = when (packageName) {
            "com.google.android.youtube" -> {
                val ids = listOf(
                    "com.google.android.youtube:id/reel_channel_name",
                    "com.google.android.youtube:id/reel_video_caption",
                    "com.google.android.youtube:id/video_title"
                )
                findTextByIds(rootNode, ids)
            }
            "com.instagram.android" -> {
                val ids = listOf(
                    "com.instagram.android:id/reels_video_view_user_name",
                    "com.instagram.android:id/reels_video_view_caption"
                )
                findTextByIds(rootNode, ids)
            }
            "com.twitter.android", "com.x.android" -> {
                val ids = listOf(
                    "com.twitter.android:id/tweet_text_view",
                    "com.twitter.android:id/screen_name"
                )
                findTextByIds(rootNode, ids)
            }
            else -> null
        }
        
        return specificId ?: findHeuristicIdentifier(rootNode)
    }

    private fun findTextByIds(rootNode: AccessibilityNodeInfo, ids: List<String>): String? {
        val texts = ids.mapNotNull { id ->
            val nodes = rootNode.findAccessibilityNodeInfosByViewId(id)
            val text = nodes.firstOrNull()?.text?.toString()
            nodes.forEach { it.recycle() }
            text
        }
        return if (texts.isEmpty()) null else texts.joinToString("|")
    }

    private fun findHeuristicIdentifier(node: AccessibilityNodeInfo?): String? {
        if (node == null) return null
        
        val text = node.text?.toString()
        if (!text.isNullOrBlank() && text.length > 5) {
            // Exclude strings that look like timestamps or progress (e.g., "00:15", "1:23 / 4:56")
            if (!text.contains(":") && !text.contains("/")) {
                return text
            }
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val childId = findHeuristicIdentifier(child)
            if (childId != null) return childId
        }
        return null
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                delay(1000)
                _sessionTime.value += 1
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}
