package com.timer.app.stopwatch

data class LapRecord(
    val number: Int,
    val elapsedMs: Long,
    val splitMs: Long
)

data class StopwatchState(
    val elapsedMs: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<LapRecord> = emptyList()
) {
    val minutes: String
        get() = String.format("%02d", (elapsedMs / 60_000) % 100)

    val seconds: String
        get() = String.format("%02d", (elapsedMs / 1_000) % 60)

    val centiseconds: String
        get() = String.format("%02d", (elapsedMs / 10) % 100)

    val hasStarted: Boolean
        get() = elapsedMs > 0L || isRunning
}
