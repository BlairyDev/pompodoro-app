package com.example.pomopodorotimer.model

data class TimerSession(
    val id: String = "",
    val sessionType: String = "",
    val plannedMinutes: Long = 0,
    val completedMinutes: Long = 0,
    val completed: Boolean = false,
    val completedAt: Long = 0
)
