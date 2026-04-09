package com.example.pomopodorotimer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(session: SessionEntity)

    @Query("SELECT * FROM sessions ORDER BY completedAt DESC")
    suspend fun getAllSessions(): List<SessionEntity>

    @Query("SELECT COUNT(*) FROM sessions WHERE completed = 1")
    suspend fun getCompletedSessionCount(): Int

    @Query("SELECT COALESCE(SUM(completedMinutes), 0) FROM sessions WHERE completed = 1")
    suspend fun getTotalFocusMinutes(): Long
}