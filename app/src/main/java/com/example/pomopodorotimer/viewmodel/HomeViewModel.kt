package com.example.pomopodorotimer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pomopodorotimer.model.SessionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _selectedDuration = MutableStateFlow(SessionType.TEN)
    private val _timeLeft = MutableStateFlow<Long>(SessionType.TEN.minutes * 60)
    val timeLeft: StateFlow<Long> = _timeLeft

    private val _displayTime = MutableStateFlow(SessionType.TEN.displayTime)
    val displayTime: StateFlow<String> = _displayTime

    private val _isPaused = MutableStateFlow(true)
    val isPaused: StateFlow<Boolean> = _isPaused

    fun convertSecondsToActualTime() {
        val duration = _timeLeft.value.seconds

        _displayTime.value = duration.toComponents { _, minutes, seconds, _ ->
            String.format(Locale.getDefault(), "%02d:%02d",  minutes, seconds)
        }
    }

    fun onTimeChange() {
        _timeLeft.value--
        convertSecondsToActualTime()
    }

    fun onPausedChange() {
        _isPaused.value = true
    }

    fun onResumeChange() {
        _isPaused.value = false
    }

    fun resetTimer() {
        _timeLeft.value = _selectedDuration.value.minutes
        _displayTime.value = _selectedDuration.value.displayTime
        _isPaused.value = true
    }

    fun onChangeDurationClick(durationSession: SessionType) {
        val seconds = durationSession.minutes * 60
        _selectedDuration.value = durationSession
        _timeLeft.value = seconds
        _displayTime.value = durationSession.displayTime
        _isPaused.value = true
    }


}