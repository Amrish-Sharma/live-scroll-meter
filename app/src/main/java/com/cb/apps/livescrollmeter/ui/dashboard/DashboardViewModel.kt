package com.cb.apps.livescrollmeter.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cb.apps.livescrollmeter.data.local.SessionDao
import com.cb.apps.livescrollmeter.domain.manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sessionDao: SessionDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    val totalSwipes: StateFlow<Int> = combine(
        sessionDao.getTotalSwipeCount().map { it ?: 0 },
        sessionManager.swipeCount
    ) { total, current ->
        total + current
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalDuration: StateFlow<Long> = combine(
        sessionDao.getTotalDuration().map { it ?: 0L },
        sessionManager.sessionTime
    ) { total, current ->
        total + current
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val activeApp: StateFlow<String?> = sessionManager.activePackage
}
