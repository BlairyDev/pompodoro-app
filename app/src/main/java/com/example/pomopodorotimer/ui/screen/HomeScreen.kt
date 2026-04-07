package com.example.pomopodorotimer.ui.screen

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pomopodorotimer.R
import com.example.pomopodorotimer.model.SessionType
import com.example.pomopodorotimer.ui.theme.PomopodoroTimerTheme
import com.example.pomopodorotimer.viewmodel.HomeViewModel
import com.example.pomopodorotimer.viewmodel.NoiseType
import com.example.pomopodorotimer.viewmodel.TimerMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val timeLeft by homeViewModel.timeLeft.collectAsStateWithLifecycle()
    val displayTime by homeViewModel.displayTime.collectAsStateWithLifecycle()
    val isPaused by homeViewModel.isPaused.collectAsStateWithLifecycle()
    val selectedNoise by homeViewModel.selectedNoise.collectAsStateWithLifecycle()
    val mode by homeViewModel.mode.collectAsStateWithLifecycle()
    val interval by homeViewModel.interval.collectAsStateWithLifecycle()
    val hasStarted by homeViewModel.hasStarted.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Audio Track setup
    val sampleRate = 44100
    val audioTrack = remember {
        val minBufferSizeInBytes = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSizeInBytes * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (audioTrack.state == AudioTrack.STATE_INITIALIZED) {
                    audioTrack.stop()
                    audioTrack.release()
                }
            } catch (e: Exception) {
                // Already released
            }
        }
    }

    // Persistent audio loop that reacts to state changes without restarting
    val currentIsPaused by rememberUpdatedState(isPaused)
    val currentSelectedNoise by rememberUpdatedState(selectedNoise)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val buffer = ShortArray(1024)
            var lastOut = 0f 
            var holdValue = 0f
            var holdCount = 0
            val holdLimit = 8 
            
            try {
                while (isActive) {
                    if (!currentIsPaused && currentSelectedNoise != NoiseType.NONE) {
                        if (audioTrack.playState != AudioTrack.PLAYSTATE_PLAYING) {
                            audioTrack.play()
                        }
                        
                        for (i in buffer.indices) {
                            if (currentSelectedNoise == NoiseType.BROWN) {
                                if (holdCount >= holdLimit) {
                                    holdValue = Random.nextFloat() * 2f - 1f
                                    holdCount = 0
                                }
                                holdCount++
                                lastOut = lastOut + 0.15f * (holdValue - lastOut)
                                buffer[i] = (lastOut * Short.MAX_VALUE * 0.8f)
                                    .toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            } else {
                                val white = Random.nextFloat() * 2f - 1f
                                buffer[i] = (white * Short.MAX_VALUE * 0.4f)
                                    .toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            }
                        }
                        audioTrack.write(buffer, 0, buffer.size)
                    } else {
                        if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                            audioTrack.pause()
                            audioTrack.flush()
                        }
                        delay(100) // Idle check
                    }
                }
            } finally {
                try {
                    if (audioTrack.state == AudioTrack.STATE_INITIALIZED) {
                        audioTrack.pause()
                        audioTrack.flush()
                    }
                } catch (e: Exception) { /* ignored */ }
            }
        }
    }

    HomeContent(
        timeLeft = timeLeft,
        displayTime = displayTime,
        isPaused = isPaused,
        hasStarted = hasStarted,
        selectedNoise = selectedNoise,
        mode = mode,
        interval = interval,
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
        onCancelConfirmed = {
            homeViewModel.resetAll()
        },
        onChangeDurationClick = homeViewModel::onChangeDurationClick,
        onNoiseSelected = homeViewModel::onNoiseSelected
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    timeLeft: Long,
    displayTime: String,
    isPaused: Boolean,
    hasStarted: Boolean,
    selectedNoise: NoiseType,
    mode: TimerMode,
    interval: Int,
    onTimeChange: () -> Unit,
    onPausedChange: () -> Unit,
    onResumeChange: () -> Unit,
    onCancelConfirmed: () -> Unit,
    onChangeDurationClick: (SessionType) -> Unit,
    onNoiseSelected: (NoiseType) -> Unit,
    resetTimer: () -> Unit,
) {
    // Determine if the timer is at its initial state for the current session type
    val isAtStart = timeLeft == when (mode) {
        TimerMode.WORK -> SessionType.entries
            .first { it == SessionType.TEN || it == SessionType.TWENTY_FIVE || it == SessionType.FIFTY }
            .minutes * 60
        TimerMode.BREAK -> timeLeft // or just false
    }
    LaunchedEffect(key1 = timeLeft, key2 = isPaused) {
        while (timeLeft > 0 && !isPaused) {
            delay(1000L)
            onTimeChange()
        }
    }

    var showCancelDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFFCDA564))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(R.drawable.image), contentDescription = "logo")
        
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Time left: $displayTime",
            fontSize = 40.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "${mode.name} • Interval $interval/4",
            fontSize = 18.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (isPaused) {
                        onResumeChange()
                    } else {
                        onPausedChange()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2B2B2B),
                    contentColor = Color.White
                )
            ) {
                val buttonText = when {
                    !isPaused -> "Pause"
                    isAtStart -> "Start"
                    else -> "Resume"
                }
                Text(text = buttonText)
            }

        }

        if (hasStarted) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        resetTimer()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2B2B2B),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Reset")
                }

                Spacer(
                    modifier = Modifier.padding(10.dp)
                )

                Button(
                    onClick = { showCancelDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2B2B2B),
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancel")
                }

                if (showCancelDialog) {
                    AlertDialog(
                        containerColor = Color(0xFFCDA564),
                        onDismissRequest = { showCancelDialog = false },
                        title = { Text("Cancel Timer") },
                        text = { Text("Are you sure?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showCancelDialog = false
                                    onCancelConfirmed()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2B2B2B),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showCancelDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2B2B2B),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }

            }

        }
        
        Spacer(modifier = Modifier.height(24.dp))

        if (!hasStarted) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(
                    onClick = { onChangeDurationClick(SessionType.TEN) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2B2B2B),
                        contentColor = Color.White
                    )
                ) {
                    Text("${SessionType.TEN.minutes} mins")
                }
                Button(
                    onClick = { onChangeDurationClick(SessionType.TWENTY_FIVE) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2B2B2B),
                        contentColor = Color.White
                    )
                ) {
                    Text("${SessionType.TWENTY_FIVE.minutes} mins")
                }
                Button(
                    onClick = { onChangeDurationClick(SessionType.FIFTY) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2B2B2B),
                        contentColor = Color.White
                    )
                ) {
                    Text("${SessionType.FIFTY.minutes} mins")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Focus Sound", fontSize = 20.sp)
        
        Spacer(modifier = Modifier.height(8.dp))

        // Segmented Switch UI
        Surface(
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.Gray),
            color = Color.Transparent
        ) {
            Row {
                val whiteSelected = selectedNoise == NoiseType.WHITE
                val brownSelected = selectedNoise == NoiseType.BROWN
                
                Button(
                    onClick = { onNoiseSelected(NoiseType.WHITE) },
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 0.dp, bottomEnd = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (whiteSelected) Color.DarkGray else Color.Transparent,
                        contentColor = if (whiteSelected) Color.White else Color.Gray
                    ),
                    modifier = Modifier.width(140.dp)
                ) {
                    Text("White")
                }
                
                Button(
                    onClick = { onNoiseSelected(NoiseType.BROWN) },
                    shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 24.dp, bottomEnd = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (brownSelected) Color.DarkGray else Color.Transparent,
                        contentColor = if (brownSelected) Color.White else Color.Gray
                    ),
                    modifier = Modifier.width(140.dp)
                ) {
                    Text("Brown")
                }
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
            isPaused = true,
            hasStarted = true,
            selectedNoise = NoiseType.NONE,
            onTimeChange = {},
            onPausedChange = {},
            onResumeChange = {},
            onCancelConfirmed = {},
            onChangeDurationClick = {},
            onNoiseSelected = {},
            mode = TimerMode.WORK,
            interval = 1,
            resetTimer = {}
        )
    }
}
