package com.example.pomopodorotimer.data.repository

import com.example.pomopodorotimer.data.local.SessionDao
import com.example.pomopodorotimer.data.local.SessionEntity
import com.example.pomopodorotimer.data.local.TimerPreferencesDao
import com.example.pomopodorotimer.data.local.TimerPreferencesEntity
import com.example.pomopodorotimer.model.SessionType
import com.example.pomopodorotimer.model.UserStats
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRepositoryReal @Inject constructor(
    private val sessionDao: SessionDao,
    private val timerPreferencesDao: TimerPreferencesDao
) : TimerRepository {

    override suspend fun updateDefaultSession(sessionType: SessionType) {
        timerPreferencesDao.savePreferences(
            TimerPreferencesEntity(
                id = 1,
                defaultSessionMinutes = sessionType.minutes
            )
        )
    }

    override suspend fun getDefaultSessionMinutes(): Long {
        return timerPreferencesDao.getPreferences()
            ?.defaultSessionMinutes ?: 25L
    }

    override suspend fun saveCompletedSession(
        sessionType: SessionType,
        completedMinutes: Long
    ) {
        val session = SessionEntity(
            sessionType = sessionType.name,
            plannedMinutes = sessionType.minutes,
            completedMinutes = completedMinutes,
            completed = true,
            completedAt = System.currentTimeMillis()
        )
        sessionDao.insertSession(session)
    }

    override suspend fun getUserStats(): UserStats {
        val totalSessions = sessionDao.getCompletedSessionCount()
        val totalFocusMinutes = sessionDao.getTotalFocusMinutes()

        return UserStats(
            totalSessions = totalSessions,
            totalFocusMinutes = totalFocusMinutes
        )
    }
}
