package com.example.pomopodorotimer.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pomopodorotimer.model.SessionType
import com.example.pomopodorotimer.ui.theme.PomopodoroTimerTheme
import com.example.pomopodorotimer.viewmodel.AuthState
import com.example.pomopodorotimer.viewmodel.AuthViewModel
import com.example.pomopodorotimer.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val timeLeft by homeViewModel.timeLeft.collectAsStateWithLifecycle()
    val displayTime by homeViewModel.displayTime.collectAsStateWithLifecycle()
    val isPaused by homeViewModel.isPaused.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Unauthenticated -> onBackClicked()
            is AuthState.Error -> Toast.makeText(context,
                (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

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
        onSignOutClicked = {
            authViewModel.signOut()
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
    onSignOutClicked: () -> Unit,
    onChangeDurationClick: (SessionType) -> Unit,
    resetTimer: () -> Unit,
) {

    LaunchedEffect(key1 = timeLeft, key2 = isPaused) {
        while (timeLeft > 0 && !isPaused) {
            delay(1000L)
            onTimeChange()
        }
    }
    Button(
        onClick = {
            onSignOutClicked()
        }
    ) {
        Text("Sign out")
    }
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Time left: $displayTime",
            fontSize = 40.sp
        )

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
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

            Spacer(
                modifier = Modifier.padding(10.dp)
            )

            Button(
                onClick = {
                    resetTimer()
                }
            ) {
                Text(text = "Reset")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
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

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    PomopodoroTimerTheme {
        HomeContent(
            timeLeft = 0,
            displayTime = "10:00",
            isPaused = false,
            onTimeChange = {},
            onPausedChange = {},
            onResumeChange = {},
            onSignOutClicked = {},
            onChangeDurationClick = {},
            resetTimer = {}
        )
    }
}