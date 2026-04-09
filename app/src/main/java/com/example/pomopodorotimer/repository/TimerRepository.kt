package com.example.pomopodorotimer.data.repository

import com.example.pomopodorotimer.model.SessionType
import com.example.pomopodorotimer.model.UserStats

interface TimerRepository {

    suspend fun updateDefaultSession(sessionType: SessionType)

    suspend fun getDefaultSessionMinutes(): Long

    suspend fun saveCompletedSession(
        sessionType: SessionType,
        completedMinutes: Long
    )

    suspend fun getUserStats(): UserStats
}
