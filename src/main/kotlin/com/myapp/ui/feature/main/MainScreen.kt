package com.myapp.ui.feature.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myapp.ui.value.R


@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    var isStarted by remember { mutableStateOf(true) }
    val currentIpAddress by viewModel.currentIp.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = currentIpAddress,
                style = MaterialTheme.typography.h3
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = {
                    viewModel.startServer()
                    viewModel.showIp()
                    isStarted = false
                },
                enabled = isStarted
            ) {
                Text(text = "Start Server")
            }
            Button(
                onClick = {
                    viewModel.stopServer()
                    viewModel.showIp()
                    isStarted = true
                },
                enabled = !isStarted
            ) {
                Text(text = "Stop Server")
            }
        }
    }
}