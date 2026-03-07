package com.cb.apps.livescrollmeter.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.cb.apps.livescrollmeter.domain.manager.SessionManager

@Composable
fun ScrollMeterOverlay(sessionManager: SessionManager) {
    val swipeCount by sessionManager.swipeCount.collectAsState()
    val sessionTime by sessionManager.sessionTime.collectAsState()

    Row(
        modifier = Modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bubble 1: Time
        Bubble(text = formatTime(sessionTime))

        // Bubble 2: Swipe Count
        Bubble(text = "↑ $swipeCount")
    }
}

@Composable
fun Bubble(text: String) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.7f)),
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
