package com.example.pomopodorotimer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomopodorotimer.data.repository.AuthRepository
import com.example.pomopodorotimer.data.repository.TimerRepository
import com.example.pomopodorotimer.model.SessionType
import com.example.pomopodorotimer.model.TimerSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val timerRepository: TimerRepository
) : ViewModel() {

    private val _selectedSession = MutableStateFlow(SessionType.TWENTY_FIVE)
    val selectedSession: StateFlow<SessionType> = _selectedSession.asStateFlow()

    private val _timeLeft = MutableStateFlow(SessionType.TWENTY_FIVE.minutes * 60)
    val timeLeft: StateFlow<Long> = _timeLeft.asStateFlow()

    private val _displayTime = MutableStateFlow(formatTime(_timeLeft.value))
    val displayTime: StateFlow<String> = _displayTime.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    fun onTimeChange() {
        if (_timeLeft.value > 0) {
            _timeLeft.value -= 1
            _displayTime.value = formatTime(_timeLeft.value)

            if (_timeLeft.value == 0L) {
                saveCompletedSession()
            }
        }
    }

    fun onPausedChange() {
        _isPaused.value = true
    }

    fun onResumeChange() {
        _isPaused.value = false
    }

    fun resetTimer() {
        _timeLeft.value = _selectedSession.value.minutes * 60
        _displayTime.value = formatTime(_timeLeft.value)
        _isPaused.value = false
    }

    fun onChangeDurationClick(sessionType: SessionType) {
        _selectedSession.value = sessionType
        _timeLeft.value = sessionType.minutes * 60
        _displayTime.value = formatTime(_timeLeft.value)
        _isPaused.value = false

        val uid = authRepository.getUserId()
        if (uid.isNotBlank()) {
            viewModelScope.launch {
                timerRepository.updateDefaultSession(uid, sessionType)
            }
        }
    }

    private fun saveCompletedSession() {
        val uid = authRepository.getUserId()
        if (uid.isBlank()) return

        val session = TimerSession(
            sessionType = _selectedSession.value.name,
            plannedMinutes = _selectedSession.value.minutes,
            completedMinutes = _selectedSession.value.minutes,
            completed = true,
            completedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            timerRepository.saveCompletedSession(uid, session)
        }
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}

