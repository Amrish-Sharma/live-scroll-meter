package com.cb.apps.livescrollmeter.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val swipeCount by viewModel.swipeCount.collectAsStateWithLifecycle()
    val sessionTime by viewModel.sessionTime.collectAsStateWithLifecycle()
    val activePackage by viewModel.activePackage.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Live Scroll Meter",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Swipes: $swipeCount",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Session Time: ${sessionTime}s",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Active App: ${activePackage ?: "-"}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
