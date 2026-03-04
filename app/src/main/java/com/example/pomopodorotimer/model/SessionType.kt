package com.example.pomopodorotimer.model

enum class SessionType(val minutes: Long, val displayTime: String) {
    TEN(10, "10:00"),
    TWENTY_FIVE(25, "25:00"),
    FIFTY(50, "50:00")
}