package com.cb.apps.livescrollmeter.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cb.apps.livescrollmeter.core.datastore.SettingsDataStore
import com.cb.apps.livescrollmeter.domain.manager.SessionManager

@Composable
fun ScrollMeterOverlay(
    sessionManager: SessionManager,
    settingsDataStore: SettingsDataStore
) {
    val swipeCount by sessionManager.swipeCount.collectAsStateWithLifecycle()
    val sessionTime by sessionManager.sessionTime.collectAsStateWithLifecycle()
    val timeLimitMinutes by settingsDataStore.timeLimitMinutes.collectAsState(initial = 10L)

    val isOverLimit = sessionTime > (timeLimitMinutes * 60)
    
    // Translucent colors as requested
    val bubbleColor = if (isOverLimit) {
        Color.Red.copy(alpha = 0.4f)
    } else {
        Color.Green.copy(alpha = 0.4f)
    }

    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bubble 1: Time
        Bubble(text = formatTime(sessionTime), backgroundColor = bubbleColor)

        // Bubble 2: Swipe Count
        Bubble(text = "↑ $swipeCount", backgroundColor = bubbleColor)
    }
}

@Composable
fun Bubble(text: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes > 0) {
        "${minutes}m"
    } else {
        String.format("00:%02d", remainingSeconds)
    }
}
