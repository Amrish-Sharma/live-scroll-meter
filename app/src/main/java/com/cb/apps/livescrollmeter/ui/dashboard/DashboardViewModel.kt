package com.cb.apps.livescrollmeter.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cb.apps.livescrollmeter.core.datastore.SettingsDataStore
import com.cb.apps.livescrollmeter.data.local.DailyStat
import com.cb.apps.livescrollmeter.data.local.SessionDao
import com.cb.apps.livescrollmeter.domain.manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sessionDao: SessionDao,
    private val sessionManager: SessionManager,
    private val settingsDataStore: SettingsDataStore
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

    val dailyStats: StateFlow<List<DailyStat>> = sessionDao.getDailyStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timeLimitMinutes: StateFlow<Long> = settingsDataStore.timeLimitMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10L)

    val activeApp: StateFlow<String?> = sessionManager.activePackage

    fun setTimeLimit(minutes: Long) {
        viewModelScope.launch {
            settingsDataStore.setTimeLimit(minutes)
        }
    }
}
