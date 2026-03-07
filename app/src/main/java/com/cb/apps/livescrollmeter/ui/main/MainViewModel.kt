package com.cb.apps.livescrollmeter.ui.main

import androidx.lifecycle.ViewModel
import com.cb.apps.livescrollmeter.domain.manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {
    val swipeCount: StateFlow<Int> = sessionManager.swipeCount
    val sessionTime: StateFlow<Long> = sessionManager.sessionTime
    val activePackage: StateFlow<String?> = sessionManager.activePackage
}
