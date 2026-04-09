package com.example.pomopodorotimer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_preferences")
data class TimerPreferencesEntity(
    @PrimaryKey
    val id: Int = 1,
    val defaultSessionMinutes: Long = 25L
)
