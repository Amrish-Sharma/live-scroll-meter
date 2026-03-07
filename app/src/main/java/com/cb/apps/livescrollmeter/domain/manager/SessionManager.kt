package com.cb.apps.livescrollmeter.domain.manager

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionManager {

    private var currentApp: String? = null
    private var startTime = 0L
    private var lastSwipeTime = 0L

    private val _swipeCount = MutableStateFlow(0)
    val swipeCount: StateFlow<Int> = _swipeCount

    private val _sessionTime = MutableStateFlow(0L)
    val sessionTime: StateFlow<Long> = _sessionTime

    private var timerJob: Job? = null

    fun startSession(packageName: String) {
        if (currentApp == packageName) return

        currentApp = packageName
        startTime = System.currentTimeMillis()
        _swipeCount.value = 0
        _sessionTime.value = 0

        startTimer()
    }

    fun endSession() {
        currentApp = null
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

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(1000)
                _sessionTime.value += 1000
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}