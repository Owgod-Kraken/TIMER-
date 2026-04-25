package com.timer.app.stopwatch

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StopwatchViewModel : ViewModel() {

    private val _state = MutableStateFlow(StopwatchState())
    val state: StateFlow<StopwatchState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var accumulatedMs: Long = 0L
    private var startTimestamp: Long = 0L

    fun start() {
        if (_state.value.isRunning) return
        startTimestamp = SystemClock.elapsedRealtime()
        _state.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (true) {
                val now = SystemClock.elapsedRealtime()
                val elapsed = accumulatedMs + (now - startTimestamp)
                _state.update { it.copy(elapsedMs = elapsed) }
                delay(10L)
            }
        }
    }

    fun pause() {
        if (!_state.value.isRunning) return
        timerJob?.cancel()
        timerJob = null
        val now = SystemClock.elapsedRealtime()
        accumulatedMs += (now - startTimestamp)
        _state.update { it.copy(isRunning = false, elapsedMs = accumulatedMs) }
    }

    fun reset() {
        timerJob?.cancel()
        timerJob = null
        accumulatedMs = 0L
        startTimestamp = 0L
        _state.update { StopwatchState() }
    }

    fun lap() {
        val currentElapsed = _state.value.elapsedMs
        if (currentElapsed == 0L) return
        val previousLapEnd = _state.value.laps.firstOrNull()?.elapsedMs ?: 0L
        val splitMs = currentElapsed - previousLapEnd
        val lapNumber = _state.value.laps.size + 1
        val newLap = LapRecord(
            number = lapNumber,
            elapsedMs = currentElapsed,
            splitMs = splitMs
        )
        _state.update { it.copy(laps = listOf(newLap) + it.laps) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
