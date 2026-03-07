package com.cb.apps.livescrollmeter.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val totalSwipes by viewModel.totalSwipes.collectAsState()
    val totalDuration by viewModel.totalDuration.collectAsState()
    val activeApp by viewModel.activeApp.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Live Scroll Meter",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        MetricCard(
            label = "Total Swipes",
            value = "↑ $totalSwipes"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MetricCard(
            label = "Total Scroll Time",
            value = formatDuration(totalDuration)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (activeApp != null) {
            Text(
                text = "Currently Tracking: ${activeApp?.substringAfterLast(".")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            Text(
                text = "Open YouTube or Instagram to start tracking",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun MetricCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return if (hours > 0) {
        String.format("%dh %dm %ds", hours, minutes, remainingSeconds)
    } else if (minutes > 0) {
        String.format("%dm %ds", minutes, remainingSeconds)
    } else {
        "${seconds}s"
    }
}
