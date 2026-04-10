package com.cb.apps.livescrollmeter.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cb.apps.livescrollmeter.data.local.DailyStat

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val dailyStats by viewModel.dailyStats.collectAsStateWithLifecycle()
    val activeApp by viewModel.activeApp.collectAsStateWithLifecycle()
    val timeLimitMinutes by viewModel.timeLimitMinutes.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Live Scroll Meter",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        TimeLimitSettings(
            initialLimit = timeLimitMinutes,
            onLimitSaved = { viewModel.setTimeLimit(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (dailyStats.isNotEmpty()) {
            UsageGraph(dailyStats)
            Spacer(modifier = Modifier.height(24.dp))
        }

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

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Daily Statistics",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dailyStats) { stat ->
                DailyStatItem(stat)
            }
        }
    }
}

@Composable
fun UsageGraph(dailyStats: List<DailyStat>) {
    val maxDuration = dailyStats.maxOfOrNull { it.totalDurationSeconds } ?: 1L
    val displayStats = dailyStats.takeLast(7) // Show last 7 days

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Usage (last 7 days)",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                displayStats.forEach { stat ->
                    val barHeightFactor = (stat.totalDurationSeconds.toFloat() / maxDuration).coerceAtLeast(0.05f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${stat.totalDurationSeconds / 60}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .fillMaxHeight(barHeightFactor)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        Text(
                            text = stat.date.substringAfterLast("-"),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeLimitSettings(initialLimit: Long, onLimitSaved: (Long) -> Unit) {
    var textValue by remember(initialLimit) { mutableStateOf(initialLimit.toString()) }
    var isEditing by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = { Text("Time Limit (min)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                enabled = isEditing
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                if (isEditing) {
                    textValue.toLongOrNull()?.let { 
                        onLimitSaved(it)
                        isEditing = false
                    }
                } else {
                    isEditing = true
                }
            }) {
                Text(if (isEditing) "Save" else "Update")
            }
        }
        
        Text(
            text = "Current limit: $initialLimit minutes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )
    }
}

@Composable
fun DailyStatItem(stat: DailyStat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = stat.date, fontWeight = FontWeight.Bold)
                Text(text = "↑ ${stat.totalSwipes} swipes", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = formatDuration(stat.totalDurationSeconds),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return if (hours > 0) {
        String.format("%dh %dm", hours, minutes)
    } else if (minutes > 0) {
        String.format("%dm %ds", minutes, remainingSeconds)
    } else {
        "${seconds}s"
    }
}
