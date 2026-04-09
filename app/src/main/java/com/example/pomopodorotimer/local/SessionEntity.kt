package com.example.pomopodorotimer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sessionType: String,
    val plannedMinutes: Long,
    val completedMinutes: Long,
    val completed: Boolean,
    val completedAt: Long
)
