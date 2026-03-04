package com.example.pomopodorotimer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pomopodorotimer.model.SessionType
import com.example.pomopodorotimer.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val timeLeft by homeViewModel.timeLeft.collectAsStateWithLifecycle()
    val displayTime by homeViewModel.displayTime.collectAsStateWithLifecycle()
    val isPaused by homeViewModel.isPaused.collectAsStateWithLifecycle()

    HomeContent(
        timeLeft = timeLeft,
        displayTime = displayTime,
        isPaused = isPaused,
        onTimeChange = {
            homeViewModel.onTimeChange()
        },
        onPausedChange = {
            homeViewModel.onPausedChange()
        },
        onResumeChange = {
            homeViewModel.onResumeChange()
        },
        resetTimer = {
            homeViewModel.resetTimer()
        },
        onChangeDurationClick = homeViewModel::onChangeDurationClick
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    timeLeft: Long,
    displayTime: String,
    isPaused: Boolean,
    onTimeChange: () -> Unit,
    onPausedChange: () -> Unit,
    onResumeChange: () -> Unit,
    onChangeDurationClick: (SessionType) -> Unit,
    resetTimer: () -> Unit,
) {

    LaunchedEffect(key1 = timeLeft, key2 = isPaused) {
        while (timeLeft > 0 && !isPaused) {
            delay(1000L)
            onTimeChange()
        }
    }

    Column {
        Text(text = "Time left: $displayTime")
        Button(
            onClick = {
                if (isPaused) {
                    onResumeChange()
                } else {
                    onPausedChange()
                }
            }
        ) {
            Text(text = if (isPaused) "Resume" else "Pause")
        }
        Button(
            onClick = {
                resetTimer()
            }
        ) {
            Text(text = "Reset")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    onChangeDurationClick(
                        SessionType.TEN
                    )
                }
            ) {
                Text("${SessionType.TEN.minutes} mins")
            }
            Button(
                onClick = {
                    onChangeDurationClick(
                        SessionType.TWENTY_FIVE
                    )
                }
            ) {
                Text("${SessionType.TWENTY_FIVE.minutes} mins")
            }
            Button(
                onClick = {
                    onChangeDurationClick(
                        SessionType.FIFTY
                    )
                }
            ) {
                Text("${SessionType.FIFTY.minutes} mins")
            }
        }
    }
}

@Preview
@Composable
private fun HomePreview() {
    
}