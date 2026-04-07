    package com.example.pomopodorotimer.viewmodel

    import androidx.lifecycle.ViewModel
    import com.example.pomopodorotimer.model.SessionType
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import java.util.Locale
    import javax.inject.Inject
    import kotlin.time.Duration.Companion.seconds

    enum class NoiseType {
        NONE, WHITE, BROWN
    }

    enum class TimerMode {
        WORK, BREAK
    }

    @HiltViewModel
    class HomeViewModel @Inject constructor() : ViewModel() {

        private val _selectedDuration = MutableStateFlow(SessionType.TEN)
        private val _timeLeft = MutableStateFlow<Long>(SessionType.TEN.minutes * 60)
        val timeLeft: StateFlow<Long> = _timeLeft

        private val _displayTime = MutableStateFlow(SessionType.TEN.displayTime)
        val displayTime: StateFlow<String> = _displayTime

        private val _isPaused = MutableStateFlow(true)
        val isPaused: StateFlow<Boolean> = _isPaused

        private val _selectedNoise = MutableStateFlow(NoiseType.NONE)
        val selectedNoise: StateFlow<NoiseType> = _selectedNoise

        private val _mode = MutableStateFlow(TimerMode.WORK)
        val mode: StateFlow<TimerMode> = _mode

        private val _interval = MutableStateFlow(1)
        val interval: StateFlow<Int> = _interval

        private val _hasStarted = MutableStateFlow(false)
        val hasStarted: StateFlow<Boolean> = _hasStarted

        fun convertSecondsToActualTime() {
            val duration = _timeLeft.value.seconds

            _displayTime.value = duration.toComponents { _, minutes, seconds, _ ->
                String.format(Locale.getDefault(), "%02d:%02d",  minutes, seconds)
            }
        }

        fun onTimeChange() {
            if (_timeLeft.value > 0) {
                _timeLeft.value--
                convertSecondsToActualTime()
            } else {
                handleSessionEnd()
            }
        }

        private fun handleSessionEnd() {
            if (_mode.value == TimerMode.WORK) {
                // Switch to BREAK
                _mode.value = TimerMode.BREAK
                _timeLeft.value = getBreakDuration()
            } else {
                // Break finished
                if (_interval.value < 4) {
                    _interval.value++

                    _mode.value = TimerMode.WORK
                    _timeLeft.value = _selectedDuration.value.minutes * 60
                } else {
                    // Finished all 4 intervals
                    resetAll()
                    return
                }
            }

            convertSecondsToActualTime()
        }

        fun onPausedChange() {
            _isPaused.value = true
        }

        fun onResumeChange() {
            _isPaused.value = false
            _hasStarted.value = true
        }

        fun resetTimer() {
            val seconds = _selectedDuration.value.minutes * 60
            _timeLeft.value = seconds
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

        fun onNoiseSelected(noiseType: NoiseType) {
            _selectedNoise.value = if (_selectedNoise.value == noiseType) NoiseType.NONE else noiseType
        }

        private fun getBreakDuration(): Long {
            return when (_selectedDuration.value) {
                SessionType.TEN -> 3 * 60
                SessionType.TWENTY_FIVE -> 5 * 60
                SessionType.FIFTY -> 15 * 60
            }
        }

        fun resetAll() {
            _interval.value = 1
            _mode.value = TimerMode.WORK
            _timeLeft.value = _selectedDuration.value.minutes * 60
            _displayTime.value = _selectedDuration.value.displayTime
            _isPaused.value = true
            _hasStarted.value = false
        }
    }
