package com.cb.apps.livescrollmeter.domain.manager

import android.view.accessibility.AccessibilityEvent
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

    // For heuristic swipe detection
    private var lastClassName: CharSequence? = null
    private var lastContentDesc: CharSequence? = null
    private var lastHeuristicSwipeTime = 0L

    private var timerJob: Job? = null

    fun startSession(packageName: String) {
        if (currentApp == packageName) return

        currentApp = packageName
        _activePackage.value = packageName
        _swipeCount.value = 0
        _sessionTime.value = 0
        lastClassName = null
        lastContentDesc = null
        lastHeuristicSwipeTime = 0L

        startTimer()
    }

    fun endSession() {
        val packageName = currentApp
        val count = _swipeCount.value
        val duration = _sessionTime.value

        if (packageName != null && (count > 0 || duration > 5)) {
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
        stopTimer()
    }

    fun incrementSwipe() {
        _swipeCount.value += 1
    }

    fun isThrottlePassed(): Boolean {
        val now = System.currentTimeMillis()
        return if (now - lastSwipeTime > 250) {
            lastSwipeTime = now
            true
        } else false
    }

    fun isHeuristicSwipe(event: AccessibilityEvent): Boolean {
        val now = System.currentTimeMillis()
        val className = event.className
        val contentDesc = event.contentDescription
        val changed = (className != null && className != lastClassName) ||
                (contentDesc != null && contentDesc != lastContentDesc)
        val rapid = (now - lastHeuristicSwipeTime) < 1500 // 1.5s
        val result = changed && rapid
        if (changed) {
            lastClassName = className
            lastContentDesc = contentDesc
            lastHeuristicSwipeTime = now
        }
        return result
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                delay(1000)
                _sessionTime.value += 1 // seconds
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}
